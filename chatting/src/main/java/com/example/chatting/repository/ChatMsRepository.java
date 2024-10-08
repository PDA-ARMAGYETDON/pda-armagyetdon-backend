package com.example.chatting.repository;

import com.example.chatting.domain.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMsRepository extends CrudRepository<ChatMessage, Integer> {
    List<ChatMessage> findByTeamId(int teamId);
}
