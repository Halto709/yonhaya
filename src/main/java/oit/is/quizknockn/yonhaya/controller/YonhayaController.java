package oit.is.quizknockn.yonhaya.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import oit.is.quizknockn.yonhaya.model.Room;

@Controller
public class YonhayaController {

  @Autowired
  private Room room;

  @GetMapping("/yonhaya")
  public String sample21() {
    return "4haya.html";
  }

  @GetMapping("di")
  public String sample38(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    this.room.addUser(loginUser);
    model.addAttribute("room", this.room);

    return "4haya.html";
  }

}
