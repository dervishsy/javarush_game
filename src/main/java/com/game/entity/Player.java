package com.game.entity;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Date;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name; //Имя персонажа  (до 12 знаков включительно)
    private String title; // Титул персонажа (до 30 знаков включительно)
    @Enumerated(EnumType.STRING)
    private Race race;  // Расса персонажа
    @Enumerated(EnumType.STRING)
    private Profession profession; // Профессия персонажа

    private Integer experience; // Опыт персонажа. Диапазон значений 0..10,000,000
    private Integer level; // Уровень персонажа
    private Integer untilNextLevel; // Остаток опыта до следующего уровня
    private Date birthday;  // Дата регистрации       Диапазон значений года 2000..3000 включительно
    private Boolean banned; // Забанен / не забанен

    public Player() {
    }

    public Player(Long id, String name, String title, Race race, Profession profession, Integer experience, Integer level, Integer untilNextLevel, Date birthday, Boolean banned) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.level = level;
        this.untilNextLevel = untilNextLevel;
        this.birthday = birthday;
        this.banned = banned;
    }

    public Integer getCalculatedLevel() {
        int result;
        int experience = (this.experience == null) ? 0 : this.experience;
        result = (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);
        return result;
    }

    public Integer getCalculatedNextLevel() {
        int result;
        int experience = (this.experience == null) ? 0 : this.experience;
        result = getCalculatedLevel();
        result = 50 * (result + 1) * (result + 2) - experience;
        return result;
    }
    public void updateFromRequest(Player player) {

        for (Field field : player.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value==null) field.set(this, field.get(player));
            } catch (IllegalAccessException e) {}
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race=" + race +
                ", profession=" + profession +
                ", experience=" + experience +
                ", level=" + level +
                ", untilNextLevel=" + untilNextLevel +
                ", birthday=" + birthday +
                ", birthday ms=" + getMils(birthday) +
                ", banned=" + banned +
                '}';
    }

    private long getMils(Date date) {
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

}
