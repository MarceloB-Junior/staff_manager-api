package com.api.staff_manager.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Table(name = "TB_EMPLOYEES")
public class EmployeeModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID employeeId;
    private String name;
    private String position;
    private BigDecimal salary;
    private String employeePhoto;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private DepartmentModel department;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
