package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.dto.ChatAlarmDto;
import com.example.chatting.dto.ChatMessageResponse;
import com.example.chatting.exception.ChatException;
import com.example.chatting.repository.ChatRoomRepository;
import com.example.common.dto.ApiResponse;
import com.example.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    @Value("${ag.url}")
    private String AG_URL;

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final RedisTemplate<String, ChatMessage> redisTemplateForMessage;
    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String teamServiceUrl = AG_URL + "/api/group/backend/chat-member";
    @Value("${redis.chatroom.prefix}")
    private String prefix;

    @Transactional
    public void createChatRoom(int teamId) {
        log.info("createChatRoom() 호출됨 - 팀 ID: {}", teamId);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTeamId(teamId);
        chatRoomRepository.save(chatRoom);
        log.info("채팅방이 성공적으로 생성됨 - 팀 ID: {}", teamId);
    }

    //@Cacheable(value = "chatMessages", key = "#teamId")
    public List<ChatMessageResponse> selectChatMessageList(int teamId) {
        System.out.println("30");
//        chatRoomRepository.findById(teamId).orElseThrow(
//                () -> new ChatException(ChatErrorCode.ROOM_NOT_FOUND));
        System.out.println("40");
        String key = prefix + "room:" + teamId;
        System.out.println("50");
        Set<ChatMessage> messageSet = redisTemplateForMessage.opsForZSet().range(key, 0, 99);
        System.out.println("60");
        List<ChatMessage> messages = List.copyOf(messageSet);
        System.out.println("70");
        List<ChatMessageResponse> messageDtos = messages.stream()
                .map(message -> new ChatMessageResponse(message.getUserId(), message.getName(), message.getMessage(), message.getCreatedAt()))
                .toList();
        System.out.println("80");
        return messageDtos;

    }

    //@CacheEvict(value = "chatMessages", key = "#messageDto.teamId")
    public void sendMessage(ChatMessage messageDto) {

        String key = prefix + "room:" + messageDto.getTeamId();

        Long generatedId = redisTemplateForMessage.opsForValue().increment("chatMessage:id");
        int id = generatedId != null ? generatedId.intValue() : 0;
        ChatMessage updatedMessageDto = messageDto.toBuilder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .build();

        long score = updatedMessageDto.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        redisTemplateForMessage.opsForZSet().add(key, updatedMessageDto, score);

        messageTemplate.convertAndSend("/sub/chat/room/" + messageDto.getTeamId(), updatedMessageDto);

        //MQ 전송
        ChatAlarmDto data = new ChatAlarmDto(updatedMessageDto.getId(), updatedMessageDto.getTeamId(), updatedMessageDto.getUserId(), updatedMessageDto.getName(), updatedMessageDto.getMessage());

        try {
            //json 으로 직렬화 하여 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String objToJson = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend("chat_to_alarm", objToJson);

            log.info("[{}] 알림 전송 완료", "chat_to_alarm");

        } catch (JsonProcessingException e) {
            throw new ChatException(ErrorCode.JACKSON_PROCESS_ERROR);
        }

    }


    public List<String> getTeamMemberNames(int teamId) {

        String url = teamServiceUrl + "?teamId=" + teamId;
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);


        List<String> memberNames = objectMapper.convertValue(response.getBody().getData(), new TypeReference<List<String>>() {
        });

        System.out.println(memberNames);
        return memberNames;
    }

    public String getUserName(int userId) {
        System.out.println("1");
        String url = AG_URL + "/api/group/backend/chat-name?userId=" + userId;
        System.out.println("2");
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);
        System.out.println("3");
        String name = objectMapper.convertValue(response.getBody().getData(), new TypeReference<String>() {
        });
        System.out.println("4");

        return name;
    }
}

