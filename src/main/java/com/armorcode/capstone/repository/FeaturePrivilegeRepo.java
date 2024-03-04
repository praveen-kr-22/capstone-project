package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.FeaturePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeaturePrivilegeRepo extends JpaRepository<FeaturePrivilege,Long> {

    @Query("SELECT COUNT(featurePrivilegeId) from FeaturePrivilege where employeeEmail "+
            "= :employeeEmail and featureId=(SELECT featureId from Feature "+
            "where featureName = :featureName) and privilegeId=(SELECT privilegeId from Privilege "+
            "where privilegeName = :privilegeName)")
    Long findByEmployeeEmailAndFeatureNameAndPrivilegeName(String employeeEmail,String featureName,String privilegeName);


    @Query("SELECT f.featureName, p.privilegeName FROM Feature f, Privilege p, FeaturePrivilege fp " +
            "WHERE fp.employeeEmail = :email " +
            "AND f.featureId = fp.featureId " +
            "AND p.privilegeId = fp.privilegeId")
    List<Object[]> findFeatureAndPrivilegeByEmployeeEmail(String email);
}



