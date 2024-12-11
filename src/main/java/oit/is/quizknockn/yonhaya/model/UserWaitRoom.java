package oit.is.quizknockn.yonhaya.model;

import java.util.ArrayList;

import oit.is.quizknockn.yonhaya.model.User;
import oit.is.quizknockn.yonhaya.model.UserMapper;

import org.springframework.stereotype.Component;

@Component
public class UserWaitRoom {
  private int waitingUser = 0;
  private boolean finishFlag = false;
  private ArrayList<User> userResult;

  public void clear() {
    waitingUser = 0;
    finishFlag = false;
  }

  public int getWaitingUser() {
    return waitingUser;
  }

  public void setWaitingUser(int waitingUser) {
    this.waitingUser = waitingUser;
  }

  public void setFinishFlag(boolean finishFlag) {
    this.finishFlag = finishFlag;
  }

  public boolean isFinishFlag() {
    return finishFlag;
  }

  public ArrayList<User> getUserResult() {
    return userResult;
  }

  public void setUserResult(ArrayList<User> user_result) {
    userResult = user_result;
  }

}
