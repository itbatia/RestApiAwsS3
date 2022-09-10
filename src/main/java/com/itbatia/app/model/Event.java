package com.itbatia.app.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "date_event")
    private LocalDateTime date;

    @OneToOne(optional = false)
    @JoinColumn(name = "file_id")
    private File file;

    @Column(name = "user_id")
    private Long userId;
}
