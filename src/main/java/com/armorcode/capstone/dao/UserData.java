package com.armorcode.capstone.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserData {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("photo")
    private String photo;

    @JsonProperty("orgName")
    private String orgName;

    @JsonProperty("role")
    private String role;

    @JsonProperty("featurePrivilege")
    private List<Object[]> featurePrivilege;


}


