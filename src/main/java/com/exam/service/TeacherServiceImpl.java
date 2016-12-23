package com.exam.service;

import com.exam.domain.*;
import com.exam.domain.dao.*;
import com.exam.web.response.GetClassStudents;
import com.exam.web.response.GetClassesResponse;
import com.exam.web.response.GetStuGradeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by NeilHY on 2016/11/17.
 */
@Service("teacherService")
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaperScoreRepository paperScoreRepository;

    @Override
    public Long createPaper(String paperName, Long teacherId, List<Question> questions) {// TODO: 2016/11/17 测试取出的teacher里的各个set项的值
        // TODO: 2016/11/17 测试能不能直接save paper，而不用先save各个题目，最后再放进paper里。
        Teacher teacher;
        if ((teacher = teacherRepository.findOne(teacherId)) != null) {
            Paper paper = new Paper(paperName, null, null, questions, teacher, null, null);
            paper=paperRepository.save(paper);
            /*
            Iterator<Question> ite = questions.iterator();
            while(ite.hasNext())
            {
                Question q=ite.next();
                q.setPaperOf(paper);
                questionRepository.save(q);
            }
            我感觉没那么智能  还是这样一个个插入吧*/
            return paper.getPaperId();
        }
        return null;
    }

    @Override
    public Long publishPaper(Long paperId, Date beginTime, Date endTime) {
        return null;
    }

    @Override
    public GetClassesResponse getClassList(Long teacherId) {
        Teacher teacher;
        if ((teacher=teacherRepository.findOne(teacherId)) != null) {
            return new GetClassesResponse(teacher);
        }
        return null;
    }

    @Override
    public GetClassStudents getClassStudent(Long classId) {
        StuClass stuClass;
        if ((stuClass = classRepository.findOne(classId)) != null) {
            return new GetClassStudents(stuClass.getStudentSet().size(), stuClass.getStudentSet());
        }
        return null;
    }

    @Override
    public Set<PaperScore> getClassGrade(Long StuclassId, Long Paperid) {
        return null;
    }

    @Override
    public GetStuGradeResponse getStudentGrade(Long StudentId) {
        System.out.println("开始！！！！！！！！！！！！");
        Student student;
        if ((student = studentRepository.findOne(StudentId)) != null) {
            if (!getStuGrades(student.getPaperScoreSet(),student).isEmpty()) {
                return new GetStuGradeResponse(student.getPaperScoreSet().size(), getStuGrades(student.getPaperScoreSet(),student));
            }
        }
        return null;
    }

    private List<GetStuGradeResponse.StudentGrade> getStuGrades(Set<PaperScore> paperScores,Student student){
        Iterator iterator=paperScores.iterator();
        List<GetStuGradeResponse.StudentGrade> studentGradeList = new ArrayList<>();
        GetStuGradeResponse.StudentGrade studentGrade;
        while (iterator.hasNext()) {
            PaperScore paperScore= (PaperScore) iterator.next();
            //获得在该考试中的排名：在所有该考试的成绩中排名
            //TODO 这样能不能获得该考试的id 和 考试的名字
            Paper paper=paperScore.getId().getPaper();
            Long paperId = paper.getPaperId();
            String paperName = paper.getPaperName();
            PaperScoreId paperScoreId = new PaperScoreId(paper, student);
            System.out.println("获得考试的id:"+ paperId+ "  获得考试的名字："+paperScore.getId().getPaper().getPaperName());
            System.out.println("获得该考试的所有记录："+paperScoreRepository.findOne(paperScoreId));

            int rank = getRank(paperScore.getScore(), paperScoreRepository.getAllPaperScoreByPaperId(paperId));

            System.out.println("获得考试的排名："+rank);

//            studentGrade = new GetStuGradeResponse().new StudentGrade(paperName,paperScore.getScore(),rank);
//            studentGradeList.add(studentGrade);
        }
        return studentGradeList;
    }

    private int getRank(Float grade,List<PaperScore> allStuGrades){
        int rank=1;
        for (PaperScore paperScore : allStuGrades) {
            if (grade < paperScore.getScore()) {
                rank++;
            }
        }
        return rank;
    }

    @Override
    public PaperScore getStudentPaperGrade(Long StudentId, Long PaperId) {
        return null;
    }

    @Override
    public Set<QuestionScore> getStudentAnswer(Long studentId, Long Paperid) {
        return null;
    }
}
