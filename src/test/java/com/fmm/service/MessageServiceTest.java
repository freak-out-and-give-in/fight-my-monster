package com.fmm.service;

import com.fmm.enumeration.Level;
import com.fmm.enumeration.TypeOfFight;
import com.fmm.model.Message;
import com.fmm.model.Monster;
import com.fmm.model.User;
import com.fmm.model.UserInfo;
import com.fmm.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    private UserInfo userInfo1;

    private UserInfo userInfo2;

    @BeforeEach
    void setup() {
        User user1 = new User("meerkat", "insects");
        user1.setAcceptTermsAndConditions(true);
        user1.setEnabled(true);
        user1.setId(1L);

        userInfo1 = new UserInfo(user1);

        User user2 = new User("dog23", "aqua");
        user2.setAcceptTermsAndConditions(true);
        user2.setEnabled(true);
        user2.setId(2L);

        userInfo2 = new UserInfo(user2);
    }

    @DisplayName("Get my messages")
    @Test
    void GetMessagesForMe() {
        Message message1 = new Message(userInfo1, 2L, TypeOfFight.EAT,
                "monster1", "monster2", 5L);

        Message message2 = new Message(userInfo2, 1L, TypeOfFight.BITE,
                "monster3", "monster4", 1111L);

        Message message3 = new Message(userInfo1, 2L, TypeOfFight.COLLECT,
                "monster5", "monster6", 0L);


        when(messageRepository.findAll()).thenReturn(Arrays.asList(message1, message2, message3));
        List<Message> resultMessageList = messageService.getMessagesForMe(1L);

        assertThat(resultMessageList).isEqualTo(List.of(message2));
        verify(messageRepository, times(1)).findAll();
    }

    @DisplayName("Get a message")
    @Test
    void GetMessage() {
        Message message = new Message(userInfo1, 2L, TypeOfFight.EAT,
                "toMonsterName", "fromMonsterName", 5L);
        message.setMessageId(13L);
        when(messageRepository.findById(13L)).thenReturn(Optional.of(message));

        Message resultMessage = messageService.getMessage(13L);

        assertThat(resultMessage).isEqualTo(message);
        verify(messageRepository, times(1)).findById(13L);
    }

    @DisplayName("Add a message")
    @Test
    void AddMessage() {
        Message message = new Message(userInfo1, 2L, TypeOfFight.COLLECT,
                "toMonsterName", "fromMonsterName", 52L);

        messageService.addMessage(message);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertThat(captor.getValue().getFromUserInfo()).isEqualTo(userInfo1);
        assertThat(captor.getValue().getToAccountId()).isEqualTo(2L);
        assertThat(captor.getValue().getTypeOfFight()).isEqualTo(TypeOfFight.COLLECT);
        assertThat(captor.getValue().getToMonsterName()).isEqualTo("toMonsterName");
        assertThat(captor.getValue().getFromMonsterName()).isEqualTo("fromMonsterName");
        assertThat(captor.getValue().getNuggetsForAccepting()).isEqualTo(52L);
    }

    @DisplayName("Delete a message")
    @Test
    void DeleteMessage() {
        Message message = new Message(userInfo1, 1L, TypeOfFight.EAT,
                "toMonsterName", "fromMonsterName", 0L);
        message.setMessageId(3L);

        messageService.deleteMessage(message);

        verify(messageRepository, times(1)).delete(eq(message));
    }

    @DisplayName("Delete all messages with this monster")
    @Test
    void DeleteMessagesWithThisMonster() {
        Message message1 = new Message(userInfo1, 2L, TypeOfFight.EAT,
                "Ridley", "Batman", 100000L);

        Message message2 = new Message(userInfo1, 2L, TypeOfFight.BITE,
                "Scott", "Batman", 0L);

        Message message3 = new Message(userInfo1, 2L, TypeOfFight.EAT,
                "Matt", "Bob", 888L);

        Message message4 = new Message(userInfo2, 1L, TypeOfFight.BITE,
                "Batman", "Joseph", 0L);

        Monster batmanMonster = new Monster(userInfo1, "Batman", Level.EXTRA);

        when(messageRepository.findAll()).thenReturn(Arrays.asList(message1, message2, message3, message4));
        messageService.deleteMessagesWithThisMonster(1L, batmanMonster);

        verify(messageRepository, times(1)).delete(eq(message1));
        verify(messageRepository, times(1)).delete(eq(message2));
        verify(messageRepository, times(1)).delete(eq(message4));
    }
}