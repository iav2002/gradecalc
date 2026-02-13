package com.ignacio.gradecalc.repository;
import com.ignacio.gradecalc.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    List<GradingScale> findByUniversity(String university);
}
