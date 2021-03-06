package com.exam.domain;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Created by NeilHY on 2016/11/14.
 */
@Embeddable
public class PaperScoreId implements Serializable {
    @ManyToOne
    private Paper paper;

    @ManyToOne
    private Student student;

    public PaperScoreId() {
    }

    public PaperScoreId(Paper paper, Student student) {
        this.paper = paper;
        this.student = student;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(obj==null || getClass() != obj.getClass())return false;
        PaperScoreId that= (PaperScoreId) obj;
        if(paper != null ? !paper.equals(that.paper):that.paper!=null)return false;
        if(student != null ? !student.equals(that.student):that.student!=null)return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result=(paper != null? paper.hashCode() : 0);
        result=97*result+(student!=null ? student.hashCode() : 0);
        return result;
    }
}
