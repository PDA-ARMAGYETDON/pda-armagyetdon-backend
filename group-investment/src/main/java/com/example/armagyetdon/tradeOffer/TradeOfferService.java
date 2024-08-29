package com.example.armagyetdon.tradeOffer;

import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.member.MemberRepository;
import com.example.armagyetdon.member.exception.MemberErrorCode;
import com.example.armagyetdon.member.exception.MemberException;
import com.example.armagyetdon.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.armagyetdon.tradeOffer.dto.TradeOfferDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TradeOfferService {
    private final TradeOfferRepository tradeOfferRepository;
    private final MemberRepository memberRepository;

    public void createTradeOffer(CreateTradeOfferRequest createTradeOfferRequest) {
        // 토큰으로 사용자 아이디와 모임 아이디 가져와야함
        int userId = 1;
        int teamId = 1;

        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        TradeOfferDto tradeOfferDto = TradeOfferDto.builder()
                .member(member)
                .tradeType(createTradeOfferRequest.getTradeType())
                .recentPrice(createTradeOfferRequest.getRecentPrice())
                .wantPrice(createTradeOfferRequest.getWantPrice())
                .quantity(createTradeOfferRequest.getQuantity())
                .code(createTradeOfferRequest.getCode())
                .build();

        try {
            tradeOfferRepository.save(tradeOfferDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TRADE_OFFER_SAVE_FAILED);
        }
    }
}
