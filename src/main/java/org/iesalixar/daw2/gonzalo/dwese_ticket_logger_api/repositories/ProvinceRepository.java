package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.Province;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Page<Province> findAll(Pageable pageable);

    Page<Province> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(p) > 0 FROM Province p WHERE p.code = :code")
    boolean existsProvinceByCode(@Param("code") String code);

    @Query("SELECT COUNT(p) > 0 FROM Province p WHERE p.code = :code AND p.id != :id")
    boolean existsProvinceByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}
