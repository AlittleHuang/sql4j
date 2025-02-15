package github.alittlehuang.sql4j.example;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Employee {
    @Id
    private Integer id;
    private String name;
    private Integer age;
    private Integer companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", updatable = false, insertable = false)
    private Company company;
}
