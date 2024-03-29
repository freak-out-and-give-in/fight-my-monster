package com.fmm.controller;

import com.fmm.dto.FightRequestDto;
import com.fmm.dto.MonsterDto;
import com.fmm.model.Message;
import com.fmm.model.Monster;
import com.fmm.model.User;
import com.fmm.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/fmm")
public class FightController {

    private final UserService userService;

    private final MonsterService monsterService;

    private final UserInfoService userInfoService;

    private final MessageService messageService;

    private final FightService fightService;

    private final ModelMapper modelMapper;

    @Autowired
    public FightController(UserService userService, MonsterService monsterService, UserInfoService userInfoService,
                           MessageService messageService, FightService fightService, ModelMapper modelMapper) {
        this.userService = userService;
        this.monsterService = monsterService;
        this.userInfoService = userInfoService;
        this.messageService = messageService;
        this.fightService = fightService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/fight-request")
    public ModelAndView showFightRequestPage(HttpServletRequest request, @ModelAttribute("FightRequestDto") FightRequestDto fightRequestDto) {
        User myUser = userService.getUser(request.getUserPrincipal().getName());
        Long myId = myUser.getId();

        User opponentUser = userService.getUser(fightRequestDto.getOpponentUsername());
        MonsterDto opponentMonster = convertToDto(monsterService.getMonster(opponentUser.getId(), fightRequestDto.getOpponentMonsterName()));
        List<MonsterDto> myMonsters = monsterService.getMonsters(myId).stream().map(this::convertToDto).collect(Collectors.toList());

        fightRequestDto = fightService.calculateN(fightRequestDto, myMonsters.size());

        MonsterDto myCurrentMonster = myMonsters.get(fightRequestDto.getN());
        int myChanceBarAmount = fightService.calculateMyChanceBarAmount(myCurrentMonster, opponentMonster);
        fightRequestDto.setMyChanceBarAmount(myChanceBarAmount);


        ModelAndView mav = new ModelAndView("/parts/fight/fight-request");
        mav.addObject("User", myUser);
        mav.addObject("OpponentUser", opponentUser);
        mav.addObject("MyMonsters", myMonsters);
        mav.addObject("OpponentMonster", opponentMonster);
        mav.addObject("Nuggets", userInfoService.getUserInfo(myId).getNuggets());
        mav.addObject("TypeOfFight", fightRequestDto.getTypeOfFight());

        mav.addObject("FightRequestDto", fightRequestDto);

        return mav;
    }

    @GetMapping("/messages/fight-offer")
    public ModelAndView showFightOfferPage(HttpServletRequest request, @ModelAttribute("MessageId") long messageId) {
        Message message = messageService.getMessage(messageId);
        Long myId = userService.getUser(request.getUserPrincipal().getName()).getId();

        List<MonsterDto> myMonsters = Stream.of
                (monsterService.getMonster(myId, message.getToMonsterName())).map(this::convertToDto).collect(Collectors.toList());
        MonsterDto opponentMonster = convertToDto(monsterService.getMonster(message.getFromUserInfo().getId(), message.getFromMonsterName()));

        int myChanceBarAmount = fightService.calculateMyChanceBarAmount(myMonsters.get(0), opponentMonster);

        ModelAndView mav = new ModelAndView("/parts/fight/fight-offer");
        mav.addObject("Message", message);
        mav.addObject("MyMonsters", myMonsters);
        mav.addObject("OpponentMonster", opponentMonster);
        mav.addObject("N", 0);
        mav.addObject("OptionIndex", 0);
        mav.addObject("MyChanceBarAmount", myChanceBarAmount);

        return mav;
    }


    private MonsterDto convertToDto(Monster monster) {
        return modelMapper.map(monster, MonsterDto.class);
    }

}
