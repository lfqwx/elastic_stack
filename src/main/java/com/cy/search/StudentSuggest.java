package com.cy.search;

import lombok.Data;

/**
 * @author: 𝓛.𝓕.𝓠
 */
@Data
public class StudentSuggest {
    private String input;
    private int weight = 10;//默认权重
}
