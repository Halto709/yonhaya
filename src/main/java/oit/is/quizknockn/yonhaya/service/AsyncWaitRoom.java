package oit.is.quizknockn.yonhaya.service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.beans.factory.annotation.Autowired;

import oit.is.quizknockn.yonhaya.model.UserWaitRoom;

import oit.is.quizknockn.yonhaya.model.User;
import oit.is.quizknockn.yonhaya.model.UserMapper;

@Service
public class AsyncWaitRoom {
  private final Logger logger = LoggerFactory.getLogger(AsyncWaitRoom.class);

  @Autowired
  private UserWaitRoom userWaitRoom;

  private boolean userUpdate = false;

  private final int MAX_USER_NUMBER = 2;

  @Async
  public void userWait() {
    logger.info("User Enters a Room");
    userWaitRoom.setWaitingUser(userWaitRoom.getWaitingUser() + 1);
    this.userUpdate = true;
  }

  public void userRank(ArrayList<User> UserResult) {
    userWaitRoom.setUserResult(UserResult);
    this.userUpdate = true;
  }

  public void clearWait() {
    userWaitRoom.clear();
    userUpdate = false;
  }

  public void quizFinish() {

    userWaitRoom.setFinishFlag(true);

  }

  @Async
  public void pushWaitRoom(SseEmitter emitter) {
    logger.info("pushWaitRoom start");

    while (true) {
      try {
        // 0.1s休み
        if (userUpdate == false) {
          TimeUnit.MILLISECONDS.sleep(100);
          continue;
        }

        // 新しいユーザが追加されていれば，その情報を送信
        logger.info("send(RoomUsers)");

        TimeUnit.MILLISECONDS.sleep(100);// 0.1秒STOP
        // JSONオブジェクトがクライアントに送付される
        emitter.send(userWaitRoom);
        userUpdate = false;

      } catch (Exception e) {
        // 例外の名前とメッセージだけ表示する
        logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
        // 例外が発生したらカウントとsendを終了する
        break;
      }
    }
    emitter.complete();// emitterの後始末．明示的にブラウザとの接続を一度切る．
  }

}
