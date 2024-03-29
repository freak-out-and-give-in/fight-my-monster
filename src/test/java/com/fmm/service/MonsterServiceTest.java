package com.fmm.service;

import com.fmm.enumeration.Level;
import com.fmm.enumeration.Potion;
import com.fmm.exception.CouldNotFindMonsterException;
import com.fmm.model.Monster;
import com.fmm.model.User;
import com.fmm.model.UserInfo;
import com.fmm.repository.MonsterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonsterServiceTest {

    @InjectMocks
    private MonsterService monsterService;

    @Mock
    private MonsterRepository monsterRepository;

    private UserInfo userInfo1;

    private UserInfo userInfo2;

    @BeforeEach
    void setup() {
        User user1 = new User("one", "free3");
        user1.setAcceptTermsAndConditions(true);
        user1.setEnabled(true);
        user1.setId(1L);

        userInfo1 = new UserInfo(user1);

        User user2 = new User("try", "55");
        user2.setAcceptTermsAndConditions(true);
        user2.setEnabled(true);
        user2.setId(2L);

        userInfo2 = new UserInfo(user2);
    }

    @DisplayName("Get a users monsters")
    @Test
    void GetMonsters() {
        Monster monster1 = new Monster(userInfo2, Level.CUSTOM);
        monster1.setAlive(false);
        Monster monster2 = new Monster(userInfo1, Level.SUPER);
        Monster monster3 = new Monster(userInfo2, "pumpkin", Level.EXTRA);
        Monster monster4 = new Monster(userInfo1, Level.STANDARD);

        when(monsterRepository.findAll()).thenReturn(Arrays.asList(monster1, monster2, monster3, monster4));
        List<Monster> resultMonsterList = monsterService.getMonsters(1L);

        assertThat(resultMonsterList).isEqualTo(Arrays.asList(monster2, monster4));
        verify(monsterRepository, times(1)).findAll();
    }

    @DisplayName("Get alive monsters")
    @Test
    void GetAliveMonsters() {
        Monster monster1 = new Monster(userInfo2, Level.CUSTOM);
        Monster monster2 = new Monster(userInfo1, Level.SUPER);
        Monster monster3 = new Monster(userInfo2, Level.EXTRA);
        monster3.setAlive(false);
        Monster monster4 = new Monster(userInfo1, Level.STANDARD);

        when(monsterRepository.findAll()).thenReturn(Arrays.asList(monster1, monster2, monster3, monster4));
        List<Monster> resultMonsterList = monsterService.getAliveMonsters(2L);

        assertThat(resultMonsterList).isEqualTo(List.of(monster1));
    }

    @DisplayName("Get a monster")
    @Test
    void GetMonster() {
        Monster monster1 = new Monster(userInfo2, "kin", Level.CUSTOM);
        Monster monster2 = new Monster(userInfo1, Level.SUPER);
        Monster monster3 = new Monster(userInfo2, Level.EXTRA);
        monster3.setAlive(false);
        Monster monster4 = new Monster(userInfo1, Level.STANDARD);

        when(monsterRepository.findAll()).thenReturn(Arrays.asList(monster1, monster2, monster3, monster4));
        Monster resultMonster = monsterService.getMonster(2L, monster3.getName());

        assertThat(resultMonster).isEqualTo(monster3);
        verify(monsterRepository, times(1)).findAll();
    }

    @DisplayName("Throw exception finding non-existent monster")
    @Test
    void GetMonsterByCouldNotFindMonster() {
        when(monsterRepository.findAll()).thenReturn(List.of());
        assertThrows(CouldNotFindMonsterException.class, () -> monsterService.getMonster(1L, "b"));
    }

    @DisplayName("Decrease potion use with ")
    @Nested
    class decreasePotionClass {

        private Monster monster;

        @BeforeEach
        void setupMonster() {
            monster = new Monster(userInfo1, Level.STANDARD);
        }

        @DisplayName(" a potion")
        @Test
        void DecreasePotionUse_WithPotion() {
            Potion potion = Potion.TRICKS_MAKER;
            monster.setPotion(potion.toString());
            monster.setPotionUses(potion.getUses());

            monsterService.decreasePotionUse(monster);

            ArgumentCaptor<Monster> captor = ArgumentCaptor.forClass(Monster.class);
            verify(monsterRepository).save(captor.capture());

            assertThat(captor.getValue().getPotionUses()).isEqualTo(potion.getUses() - 1);
        }

        @DisplayName(" a potion with 1 use left")
        @Test
        void DecreasePotionUse_WithPotionWith1UseLeft() {
            monster.setPotion(String.valueOf(Potion.DEMON_ATTACK));
            monster.setPotionUses(1);

            monsterService.decreasePotionUse(monster);

            ArgumentCaptor<Monster> captor = ArgumentCaptor.forClass(Monster.class);
            verify(monsterRepository).save(captor.capture());

            assertThat(captor.getValue().getPotion()).isEqualTo("");
            assertThat(captor.getValue().getPotionUses()).isEqualTo(0);
        }

        @DisplayName(" no potion")
        @Test
        void DecreasePotionUse_WithoutPotion() {
            monster.setPotion("");

            monsterService.decreasePotionUse(monster);

            when(monsterRepository.findById(1L)).thenReturn(Optional.of(monster));
            Monster resultMonster = monsterRepository.findById(1L).get();

            assertThat(resultMonster.getPotion()).isEqualTo("");
        }

    }

    @DisplayName("Add a monster")
    @Test
    void AddMonster() {
        Monster monster = new Monster(userInfo1, "er", Level.STANDARD);

        monsterService.addMonster(monster);

        ArgumentCaptor<Monster> captor = ArgumentCaptor.forClass(Monster.class);
        verify(monsterRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(monster.getId());
        assertThat(captor.getValue().getUserInfo()).isEqualTo(monster.getUserInfo());
        assertThat(captor.getValue().getName()).isEqualTo(monster.getName());
        assertThat(captor.getValue().getGenus()).isEqualTo(monster.getGenus());
        assertThat(captor.getValue().getSpecies()).isEqualTo(monster.getSpecies());
        assertThat(captor.getValue().getAttack()).isEqualTo(monster.getAttack());
        assertThat(captor.getValue().getDefence()).isEqualTo(monster.getDefence());
        assertThat(captor.getValue().getBrains()).isEqualTo(monster.getBrains());
        assertThat(captor.getValue().getTricks()).isEqualTo(monster.getTricks());
        assertThat(captor.getValue().isAlive()).isEqualTo(monster.isAlive());
        assertThat(captor.getValue().getPotion()).isEqualTo(monster.getPotion());
    }

    @DisplayName("Update a monster")
    @Test
    void UpdateMonster() {
        Monster monster = new Monster(userInfo1, "dvc", Level.STANDARD);
        monster.setPotion("TRICKS_MAKER");

        monsterService.addMonster(monster);

        ArgumentCaptor<Monster> captor = ArgumentCaptor.forClass(Monster.class);
        verify(monsterRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(monster.getId());
        assertThat(captor.getValue().getUserInfo()).isEqualTo(userInfo1);
        assertThat(captor.getValue().getName()).isEqualTo(monster.getName());
        assertThat(captor.getValue().getGenus()).isEqualTo(monster.getGenus());
        assertThat(captor.getValue().getSpecies()).isEqualTo(monster.getSpecies());
        assertThat(captor.getValue().getAttack()).isEqualTo(monster.getAttack());
        assertThat(captor.getValue().getDefence()).isEqualTo(monster.getDefence());
        assertThat(captor.getValue().getBrains()).isEqualTo(monster.getBrains());
        assertThat(captor.getValue().getTricks()).isEqualTo(monster.getTricks());
        assertThat(captor.getValue().isAlive()).isEqualTo(monster.isAlive());
        assertThat(captor.getValue().getPotion()).isEqualTo(monster.getPotion());
    }
}