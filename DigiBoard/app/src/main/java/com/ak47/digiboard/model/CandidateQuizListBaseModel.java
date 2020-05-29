package com.ak47.digiboard.model;

public class CandidateQuizListBaseModel {
    String quizId;
    String examiner;
    String quizName;
    String quizDescription;
    String quizDate;
    String endTime;
    String startTime;
    boolean isAttempted;

    public CandidateQuizListBaseModel(String quizId, String examiner, String quizName, String quizDescription, String quizDate, String endTime, String startTime, boolean isAttempted) {
        this.quizId = quizId;
        this.examiner = examiner;
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.quizDate = quizDate;
        this.endTime = endTime;
        this.startTime = startTime;
        this.isAttempted = isAttempted;
    }

    public CandidateQuizListBaseModel() {
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getExaminer() {
        return examiner;
    }

    public void setExaminer(String examiner) {
        this.examiner = examiner;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }

    public String getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(String quizDate) {
        this.quizDate = quizDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    @Override
    public String toString() {
        return "CandidateQuizListBaseModel{" +
                "quizId='" + quizId + '\'' +
                ", examiner='" + examiner + '\'' +
                ", quizName='" + quizName + '\'' +
                ", quizDescription='" + quizDescription + '\'' +
                ", quizDate='" + quizDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", isAttempted=" + isAttempted +
                '}';
    }
}
