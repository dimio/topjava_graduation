package ru.javawebinar.topjava.graduation.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "votes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date", "restaurant_id"}, name = "votes_unique_ucd_idx")})
@BatchSize(size = 200)
public class Vote extends AbstractBaseEntity {

    public static final LocalTime DECISION_TIME = LocalTime.of(11, 0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Restaurant restaurant;

    @Column(name = "date", nullable = false, columnDefinition = "date default now()")
    private @NotNull LocalDate date = LocalDate.now();

    private transient boolean isUpdated;

    public Vote() {
    }

    public Vote(Vote v) {
        this(v.getId(), v.getDate(), v.getRestaurant(), v.getUser());
    }

    public Vote(Integer id, @NotNull LocalDate date, @NotNull Restaurant restaurant, @NotNull User user) {
        super(id);
        this.restaurant = restaurant;
        this.user = user;
        this.date = date;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @NotNull LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    @Override
    public String toString() {
        return "Vote{" +
            "id=" + id +
            ", date=" + date +
            '}';
    }
}
