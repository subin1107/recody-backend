package com.recody.recodybackend.catalog.features.search;

import com.recody.recodybackend.catalog.features.PersonalizedContent;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchContentResponse {
    
    public List<? extends PersonalizedContent> contents;
    public Integer page;
    public Integer total;
    
    
}
