package com.dmytrozah.profitsoft.domain.entity.embeds;

import jakarta.persistence.Embeddable;

@Embeddable
public record AuthorLivingAddress(int postCode, int houseNum, String street, String city, String country) {
}
