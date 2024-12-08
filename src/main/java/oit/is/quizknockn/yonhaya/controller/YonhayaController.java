package oit.is.quizknockn.yonhaya.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.quizknockn.yonhaya.model.Room;
import oit.is.quizknockn.yonhaya.service.AsyncJoinRoom;
import oit.is.quizknockn.yonhaya.service.AsyncWaitRoom;
import oit.is.quizknockn.yonhaya.model.Quiz;
import oit.is.quizknockn.yonhaya.model.QuizMapper;
import oit.is.quizknockn.yonhaya.model.QuizChoices;
import oit.is.quizknockn.yonhaya.model.QuizChoicecsMapper;
import oit.is.quizknockn.yonhaya.model.User;
import oit.is.quizknockn.yonhaya.model.UserMapper;

@Controller
@RequestMapping("/yonhaya")
public class YonhayaController {

  private final Logger logger = LoggerFactory.getLogger(YonhayaController.class);

  @Autowired
  private Room room;

  @Autowired
  private AsyncJoinRoom asyncJoinRoom;

  @Autowired
  private AsyncWaitRoom asyncWaitRoom;

  @Autowired
  private QuizMapper quizMapper;

  @Autowired
  private QuizChoicecsMapper quizChoicecsMapper;

  @Autowired
  private UserMapper userMapper;

  // デモ用のクイズインデックス
  private int currentQuestionIndex = 0;
  private int j = 0;
  private int quizID = 1;
  private int finishNumber = 0;
  private final int MAX_QUESTIONS = 2;
  private final int MAX_USER_NUMBER = 2;
  private int scoreWeight = 4;

  @GetMapping("")
  public String showHomePage(Principal prin, ModelMap model) {
    User loginUser = userMapper.selectByUserName(prin.getName());
    model.addAttribute("loginUser", loginUser);
    return "4haya.html";
  }

  @GetMapping("help")
  public String showHelp() {
    return "help.html";
  }

  @GetMapping("di")
  public String joinRoom(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    if (this.room.addUser(loginUser)) {
      // ユーザがルームに追加される
      this.asyncJoinRoom.userJoin(this.room.getUsers());
      model.addAttribute("room", this.room);
      // ユーザがルームに追加されたら、isActiveをtrueにする。
      userMapper.updateByUserIsActive(loginUser, true);
      return "joinRoom.html";
    }
    model.addAttribute("error", loginUser);
    return "4haya.html";
  }

  // ルームに誰が入っているか非同期で表示
  @GetMapping("roomInfo")
  public SseEmitter roomInfo() {
    logger.info("pushRoomUsers");
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    this.asyncJoinRoom.pushRoomUsers(emitter);
    return emitter;
  }

  @GetMapping("waitInfo")
  public SseEmitter waitInfo() {
    logger.info("pushWaitRoom");
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    this.asyncWaitRoom.pushWaitRoom(emitter);
    return emitter;
  }

  @GetMapping("quiz")
  public String shiftQuiz(ModelMap model) {
    if (MAX_QUESTIONS <= currentQuestionIndex) {
      return "finish.html";
    }
    // quizIndexを1から10のランダムな値に設定
    quizID = room.getQuizOrder().get(currentQuestionIndex);
    Quiz quiz = quizMapper.selectById(quizID);
    QuizChoices quizChoices = quizChoicecsMapper.selectAllById(quizID);
    model.addAttribute("quiz", quiz);
    model.addAttribute("Choices", quizChoices);
    model.addAttribute("currentQuestionIndex", currentQuestionIndex + 1);
    j++;
    if (j == 2) {
      currentQuestionIndex++;
      j = 0;
    }
    scoreWeight = 4;

    return "quiz.html";
  }

  @PostMapping("judge")
  public String judgeQuiz(@RequestParam String choice, ModelMap model, Principal prin) {
    String result;
    String answer = quizChoicecsMapper.selectById(quizID);
    if (choice.equals(answer)) {
      result = "正解";
      userMapper.updatePointByUserName(prin.getName(), scoreWeight--);
    } else {
      result = "不正解";
    }

    asyncWaitRoom.userWait();

    if (MAX_QUESTIONS <= currentQuestionIndex) {
      finishNumber++;
    }
    if (finishNumber == MAX_USER_NUMBER) {
      asyncWaitRoom.quizFinish();
    }

    model.addAttribute("result", result);
    return "wait.html";
  }

  @GetMapping("finish")
  public String finish(ModelMap model) {
    ArrayList<User> UserResult = userMapper.selectByResult(true);

    // 挿入ソートを使用して UserResult を point で降順に並び替える
    for (int i = 1; i < UserResult.size(); i++) {
      User key = UserResult.get(i);
      int j = i - 1;

      // key の point より大きい値を右にシフトする
      while (j >= 0 && UserResult.get(j).getPoint() < key.getPoint()) {
        UserResult.set(j + 1, UserResult.get(j));
        j--;
      }
      UserResult.set(j + 1, key);
    }

    // 並び替え後に rankNumber を設定する
    int rankNumber = 1;
    for (int i = 0; i < UserResult.size(); i++) {
      // 前のユーザーとポイントが同じ場合、同じランクを設定

      if (i > 0 && UserResult.get(i).getPoint() == UserResult.get(i -
          1).getPoint()) {
        UserResult.get(i).setRank(UserResult.get(i - 1).getRank());
      } else {
        UserResult.get(i).setRank(rankNumber);
      }
      rankNumber++;

    }

    model.addAttribute("UserResult", UserResult);
    return "finish.html";
  }

  @GetMapping("exit")
  public String exit(Principal prin) {
    String loginUser = prin.getName();
    userMapper.updateByUserIsActive(loginUser, false);
    userMapper.setPointZero();
    userMapper.setRankZero();
    room.clearRoomInfo();
    currentQuestionIndex = 0;
    quizID = 1;
    asyncJoinRoom.clearuserJoin();
    asyncWaitRoom.clearWait();
    return "4haya.html";
  }

}
