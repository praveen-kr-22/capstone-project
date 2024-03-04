package com.armorcode.capstone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Feature_Privilege")
public class FeaturePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_privilege_id")
    private Long featurePrivilegeId;

    @Column(name = "feature_id")
    private Long featureId;

    @Column(name = "privilege_id")
    private Long privilegeId;

    @Column(name = "employee_email")
    private String employeeEmail;
}
