package oit.is.quizknockn.yonhaya.model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class Room {

  final int MAX_USER_NUMBER = 2;
  int roomNo = 1;
  private int maxquizINdex = 50;
  ArrayList<String> users = new ArrayList<>();
  ArrayList<Integer> quizOrder = new ArrayList<>();

  public boolean addUser(String name) {
    // 同名のユーザが居たら何もせずにreturn
    for (String s : this.users) {
      if (s.equals(name)) {
        return false;
      }
    }
    // 同名のユーザが居なかった場合はusersにnameを追加する
    this.users.add(name);

    if (users.size() == MAX_USER_NUMBER) {
      int i = 0;
      while (i < 10) {
        int randomQuizID = ThreadLocalRandom.current().nextInt(1, maxquizINdex + 1);
        if (checkNumber(randomQuizID)) {
          quizOrder.add(randomQuizID);
          i++;
        }
      }
    }
    return true;
  }

  public boolean checkNumber(int randomQuizID) {
    for (int QuizNum : quizOrder) {
      if (randomQuizID == QuizNum) {
        return false;
      }
    }
    return true;
  }

  public void clearRoomInfo() {
    roomNo++;
    users.clear();
    quizOrder.clear();
  }

  // 以降はフィールドのgetter/setter
  // これらがないとThymeleafで値を取得できない
  public int getRoomNo() {
    return roomNo;
  }

  public void setRoomNo(int roomNo) {
    this.roomNo = roomNo;
  }

  public ArrayList<String> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<String> users) {
    this.users = users;
  }

  public ArrayList<Integer> getQuizOrder() {
    return quizOrder;
  }

  public void setQuizOrder(ArrayList<Integer> n) {
    this.quizOrder = n;
  }

}
