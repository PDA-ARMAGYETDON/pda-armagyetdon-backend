package com.example.group_investment.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("UPVOTE_NUMBER")
public class ROfferUpvotesNumber extends RuleOffer {
    private int tradeUpvotes;
}
