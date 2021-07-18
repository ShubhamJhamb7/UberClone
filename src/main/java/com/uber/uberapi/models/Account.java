package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends Auditable {

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    // todo: (fetch = FetchType.EAGER)
    @ManyToMany
    private List<Role> roles = new ArrayList<>();
}
