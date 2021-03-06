package com.exam.web;

import com.exam.domain.*;
import com.exam.service.StudentService;
import com.exam.web.request.StudentUpload;
import com.exam.web.response.GetSelectPapersResponse;
import com.exam.web.response.GetStudentPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by NeilHY on 2016/11/17.
 */
@Controller
public class StudentControler {

    @Autowired
    private StudentService studentService;

    @RequestMapping(value = RequestUrls.toExam,method = RequestMethod.GET)
    @ResponseBody
    public String startExam(@PathVariable Long paperId,HttpSession session) {
        if (paperId != 0) {
            session.setAttribute("paperId",paperId);
            return "exam";
        }
        return null;
    }

    @RequestMapping(value = RequestUrls.getSelectPapers,method = RequestMethod.GET)
    @ResponseBody
    public GetSelectPapersResponse getSelectPapers(HttpSession session) {
        if (session.getAttribute("id") != null) {
            return studentService.getSelectPapers((Long) session.getAttribute("id"));
        }
        return null;
    }

    @RequestMapping(value = RequestUrls.studentGetPaper,method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public GetStudentPaper getStudentPaper(HttpSession session)//返回试卷
    {
        long paperId,studentId;
        paperId = (long) session.getAttribute("paperId");
        studentId = (long) session.getAttribute("id");
        Paper paper = studentService.getPaper(paperId);
        Student student = studentService.getStudent(studentId);
        Set<Question> questions = new HashSet<>(paper.getQuestions());
        long exam_second = paper.getPaperEnd().getTime()-new Date().getTime();

        GetStudentPaper getStudentPaper = new GetStudentPaper(paperId,paper.getPaperName(),studentId,student.getStudentName(),questions,exam_second);

        return getStudentPaper;
    }


    @RequestMapping(value = RequestUrls.studentUpload,method = RequestMethod.POST,consumes = "application/json")
    @ResponseBody
    public float studentUpload(@RequestBody StudentUpload studentUpload)//处理提交的试卷 保存并返回分数
    {
        long student_id = studentUpload.getStudent_id();
        long paper_id = studentUpload.getPaper_id();
        ArrayList<StudentUpload.Answer>answers = studentUpload.getAnswer();
        if(answers == null)//交白卷
        {
            PaperScoreId paperScoreId = new PaperScoreId(studentService.getPaper(paper_id),studentService.getStudent(student_id));
            PaperScore paperScore = new PaperScore();
            paperScore.setId(paperScoreId);

            studentService.saveAnswer(paperScore);

            return 0f;
        }
        Iterator iterator = answers.iterator();
        String selectAnswer=new String(),fillAnswer=new String(),questionAnswer=new String(),truefalseAnswer=new String();
        Float selectScore= new Float(0);
        while(iterator.hasNext())
        {
            StudentUpload.Answer answer = (StudentUpload.Answer) iterator.next();
            long question_id = answer.getAnswer_id();
            Question question=studentService.getQuestion(question_id);
            String type = question.getType();
            Float score = question.getScore();
            String rightAnswer = question.getAnswer();
            switch (type)
            {
                case "选择题":
                    selectAnswer+=question_id+"#"+answer.getAnswer_text()+"$";
                    if(rightAnswer.equals(answer.getAnswer_text()))
                        selectScore+= score;
                    break;
                case "填空题":
                    fillAnswer+=question_id+"#"+answer.getAnswer_text()+"$";
                    break;
                case "简答题":
                    questionAnswer+=question_id+"#"+answer.getAnswer_text()+"$";
                    break;
                case "判断题":
                    truefalseAnswer+=question_id+"#"+answer.getAnswer_text()+"$";
                    if(rightAnswer.equals(answer.getAnswer_text()))
                        selectScore+= score;
                    break;
            }
        }

        PaperScoreId paperScoreId = new PaperScoreId(studentService.getPaper(paper_id),studentService.getStudent(student_id));
        PaperScore paperScore = new PaperScore();
        paperScore.setId(paperScoreId);
        paperScore.setFillAnswer(fillAnswer);
        paperScore.setQuestionAnswer(questionAnswer);
        paperScore.setSelectAnswer(selectAnswer);
        paperScore.setTrueFalseAnswer(truefalseAnswer);
        paperScore.setSelectScore(selectScore);

        studentService.saveAnswer(paperScore);

        return selectScore;

    }



}
