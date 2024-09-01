package com.example.armagyetdon.team;

import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.member.MemberRepository;
import com.example.armagyetdon.rule.Rule;
import com.example.armagyetdon.rule.RuleRepository;
import com.example.armagyetdon.rule.dto.RuleDto;
import com.example.armagyetdon.rule.exception.RuleErrorCode;
import com.example.armagyetdon.rule.exception.RuleException;
import com.example.armagyetdon.team.dto.*;
import com.example.armagyetdon.team.exception.TeamErrorCode;
import com.example.armagyetdon.team.exception.TeamException;
import com.example.armagyetdon.user.User;
import com.example.armagyetdon.user.UserRepository;
import com.example.armagyetdon.user.exception.UserErrorCode;
import com.example.armagyetdon.user.exception.UserException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final RuleRepository ruleRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String baseUrl = "http://localhost:8081/";
    private final InvitationRepository invitationRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;


    @Transactional
    public CreateTeamResponse createTeam(int userId, CreateTeamRequest createTeamRequest) {
        // 팀을 만든 user
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        //1. 팀
        Team savedTeam;
        TeamDto teamDto = TeamDto.builder()
                .user(user)
                .name(createTeamRequest.getName())
                .category(createTeamRequest.getCategory())
                .startAt(createTeamRequest.getStartAt())
                .endAt(createTeamRequest.getEndAt())
                .build();
        try {
            savedTeam = teamRepository.save(teamDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.TEAM_SAVE_FAILED);
        }
        //2. 규칙
        RuleDto ruleDto = RuleDto.builder()
                .team(savedTeam)
                .prdyVrssRt(createTeamRequest.getPrdyVrssRt())
                .urgentTradeUpvotes(createTeamRequest.getUrgentTradeUpvotes())
                .tradeUpvotes(createTeamRequest.getTradeUpvotes())
                .depositAmt(createTeamRequest.getDepositAmt())
                .period(createTeamRequest.getPeriod())
                .payDate(createTeamRequest.getPayDate())
                .maxLossRt(createTeamRequest.getMaxLossRt())
                .maxProfitRt(createTeamRequest.getMaxProfitRt())
                .build();
        try {
            ruleRepository.save(ruleDto.toEntity());
        } catch (Exception e) {
            throw new RuleException(RuleErrorCode.RULE_SAVE_FAILED);
        }
        //3 초대코드 생성
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        String inviteCode = code.toString();

        //4 초대링크 생성
        String inviteUrl = baseUrl + inviteCode;

        //5 초대정보 저장
        InvitationDto invitationDto = InvitationDto.builder()
                .team(savedTeam)
                .inviteCode(inviteCode)
                .inviteUrl(inviteUrl)
                .build();
        try {
            invitationRepository.save(invitationDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.INVITATION_SAVE_FAILED);
        }
        return new CreateTeamResponse(inviteCode, inviteUrl);
    }

//    public SelectTeamResponse selectTeam(int id) {
//        SelectTeamResponse selectTeamResponse = teamRepository.findById(id);
//        return selectTeamResponse;
//    }
    public InsertCodeTeamResponse insertCode(String inviteCode) {

        Invitation invitation = invitationRepository.findByInviteCode(inviteCode).orElseThrow(()
                -> new TeamException(TeamErrorCode.INVITATION_NOT_FOUND));

        return new InsertCodeTeamResponse(invitation.getId());
    }

    @Transactional
    public DetailPendingTeamResponse selectPendingDetails(int groupId, int userId) {
        //1. 상세 조회 (모임, 규칙, 인원수)
        //2-1. 모임 조회
        Team team = teamRepository.findById(groupId).orElseThrow(() -> new TeamException(TeamErrorCode.GROUP_NOT_FOUND));
        TeamDto teamDto = team.fromEntity(team);
        //2-2. 규칙 조회
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        RuleDto ruleDto = rule.fromEntity(rule);
        //2-3. is 모임장
        int isLeader = 0;
        if (teamDto.getUser().getId() == userId)
            isLeader = 1;
        //2-4. is 참여
        int isParticipating = 0;
        if (isLeader == 0) {
            List<Member> members = memberRepository.findByTeam(team);
            for (Member member : members) {
                if (member.getUser().getId() == userId) {
                    isParticipating = 1;
                    break;
                }
            }
        }
        //2-5. 인원수 조회
        // FIXME : 멤버
        int invitedMembers = memberRepository.countByTeam(team);

        return DetailPendingTeamResponse.builder()
                .name(teamDto.getName())
                .category(teamDto.getCategory())
                .startAt(teamDto.getStartAt())
                .endAt(teamDto.getEndAt())
                .prdyVrssRt(ruleDto.getPrdyVrssRt())
                .urgentTradeUpvotes(ruleDto.getUrgentTradeUpvotes())
                .tradeUpvotes(ruleDto.getTradeUpvotes())
                .depositAmt(ruleDto.getDepositAmt())
                .period(ruleDto.getPeriod())
                .payDate(ruleDto.getPayDate())
                .maxLossRt(ruleDto.getMaxLossRt())
                .maxProfitRt(ruleDto.getMaxProfitRt())
                .invitedMembers(invitedMembers)
                .isLeader(isLeader)
                .isParticipating(isParticipating)
                .build();
    }

}
