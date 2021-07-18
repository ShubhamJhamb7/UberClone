package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car")
public class Car extends Auditable {

    @ManyToOne
    private Colour color;

    private String plateNumber;

    private String brandaAndModel;

    @Enumerated(value = EnumType.STRING)
    private CarType carType;

    @OneToOne
    private Driver driver;
}
