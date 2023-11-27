package com.fmm.model;

import com.fmm.enumeration.Level;
import com.fmm.enumeration.Species;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "monsters")
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "monster_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "genus", nullable = false)
    private String genus;

    @Column(name = "species", nullable = false)
    private String species;

    @Column(name = "attack", nullable = false)
    private long attack;

    @Column(name = "defence", nullable = false)
    private long defence;

    @Column(name = "brains", nullable = false)
    private long brains;

    @Column(name = "tricks", nullable = false)
    private long tricks;

    @Column(name = "alive", nullable = false)
    private boolean alive = true;

    @Column(name = "potion_equipped")
    private String potionEquipped;

    public Monster() {
    }

    public Monster(User user, String name, Level level) {
        this.user = user;

        this.species = returnWordAsEnum(generateRandomSpecies());
        this.genus = returnWordAsEnum(String.valueOf(Species.valueOf(species).getGenus()));

        if (name.isEmpty()) {
            this.name = returnWordAsPascalCase(String.valueOf(species)) + " " + generateRandomEndOfName();
        } else {
            this.name = returnWordAsPascalCase(name);
        }

        this.attack = generateRandomStat(level);
        this.defence = generateRandomStat(level);
        this.brains = generateRandomStat(level);
        this.tricks = generateRandomStat(level);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public long getAttack() {
        return attack;
    }

    public void setAttack(long attack) {
        this.attack = attack;
    }

    public long getDefence() {
        return defence;
    }

    public void setDefence(long defence) {
        this.defence = defence;
    }

    public long getBrains() {
        return brains;
    }

    public void setBrains(long brains) {
        this.brains = brains;
    }

    public long getTricks() {
        return tricks;
    }

    public void setTricks(long tricks) {
        this.tricks = tricks;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getPotionEquipped() {
        return potionEquipped;
    }

    public void setPotionEquipped(String potionEquipped) {
        this.potionEquipped = potionEquipped;
    }

    private long generateRandomStat(Level level) {
        Random random = new Random();

        if (level.equals(Level.STANDARD)) {
            return random.nextInt(50, 300);
        } else if (level.equals(Level.EXTRA)) {
            return random.nextInt(300, 1000);
        } else if (level.equals(Level.SUPER)) {
            return random.nextInt(1000, 5000);
        } else { //CUSTOM
            return random.nextInt(51, 301);
        }
    }

    private String generateRandomSpecies() {
        List<Species> speciesList = new ArrayList<>(List.of(Species.values()));
        Collections.shuffle(speciesList);

        return speciesList.get(10).toString();
    }

    private static String generateRandomEndOfName() {
        Random randomNum = new Random();
        StringBuilder bufferNumber = new StringBuilder(3);

        int numberOrNot = randomNum.nextInt(10);
        while (numberOrNot > 4) { //> 4 gives 50% chance, cumulative btw
            randomNum = new Random();
            int n = randomNum.nextInt(10);
            bufferNumber.append(n);
            numberOrNot = randomNum.nextInt(10);

            if (bufferNumber.length() >= 3) {
                break;
            }
        }

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random randomLet = new Random();
        int letterLimit = 6 - bufferNumber.length();

        StringBuilder bufferLetter = new StringBuilder(letterLimit);

        for (int i = 0; i < letterLimit; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (randomLet.nextFloat() * (rightLimit - leftLimit + 1));
            bufferLetter.append((char) randomLimitedInt);
        }

        StringBuilder stringTemplate = new StringBuilder(6);
        int numberCounter = 0;
        int letterCounter = 0;
        while (stringTemplate.length() < 6) {
            Random randomTotal = new Random();
            int k = randomTotal.nextInt(2);

            if (numberCounter >= bufferNumber.length()) {
                k = 1;
            } else if (letterCounter >= bufferLetter.length()) {
                k = 0;
            }

            if (k == 0) {
                stringTemplate.append(bufferNumber.charAt(numberCounter));
                numberCounter++;
            } else {
                stringTemplate.append(bufferLetter.charAt(letterCounter));
                letterCounter++;
            }
        }

        return stringTemplate.toString().toUpperCase();
    }

    private String returnWordAsPascalCase(String word) {
        word = word.toLowerCase();
        word = word.substring(0, 1).toUpperCase() + word.substring(1);

        if (word.contains("_")) {
            int underscoreIndex = word.indexOf("_");
            word = word.replace("_", " ");

            String firstLetterOfSecondWord = String.valueOf(word.charAt(underscoreIndex + 1)).toUpperCase();
            word = word.substring(0, underscoreIndex + 1) + firstLetterOfSecondWord + word.substring(underscoreIndex + 2);
        }

        return word;
    }

    private String returnWordAsEnum(String word) {
        word = word.toUpperCase();
        word = word.replace(" ", "_");

        return word;
    }
}