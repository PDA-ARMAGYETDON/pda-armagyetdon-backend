package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetROfferResponseUrgentSale implements GetROfferResponseType {

    @Enumerated(EnumType.STRING)
    RuleType type;
    int id;
    int upvotes;
    int downvotes;
    int totalvotes;

    int tradeUpvotes;
    double prdyVrssRt;

    boolean isVote;

    public GetROfferResponseUrgentSale(RuleType type, int id, int upvotes, int downvotes, int totalvotes, int tradeUpvotes, double prdyVrssRt, boolean isVote) {
        this.type = type;
        this.id = id;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.tradeUpvotes = tradeUpvotes;
        this.prdyVrssRt = prdyVrssRt;
        this.isVote = isVote;
    }

}
