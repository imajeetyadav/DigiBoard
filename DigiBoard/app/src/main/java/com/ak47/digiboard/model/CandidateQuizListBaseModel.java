package com.ak47.digiboard.model;

public class CandidateQuizListBaseModel {
    String quizId;
    String examiner;
    String quizName;
    String quizDescription;
    String quizDate;
    String endTime;
    String startTime;
    String quizStartTime;
    String quizEndTime;
    String result;
    boolean isAttempted;

    public CandidateQuizListBaseModel(String quizId, String examiner, String quizName, String quizDescription, String quizDate, String endTime, String startTime, String quizStartTime, String quizEndTime, String result, boolean isAttempted) {
        this.quizId = quizId;
        this.examiner = examiner;
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.quizDate = quizDate;
        this.endTime = endTime;
        this.startTime = startTime;
        this.quizStartTime = quizStartTime;
        this.quizEndTime = quizEndTime;
        this.result = result;
        this.isAttempted = isAttempted;
    }

    //    public CandidateQuizListBaseModel(String quizId, String examiner, String quizName, String quizDescription, String quizDate, String endTime, String startTime, boolean isAttempted) {
//        this.quizId = quizId;
//        this.examiner = examiner;
//        this.quizName = quizName;
//        this.quizDescription = quizDescription;
//        this.quizDate = quizDate;
//        this.endTime = endTime;
//        this.startTime = startTime;
//        this.isAttempted = isAttempted;
//    }

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

    public boolean getIsAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public String getQuizStartTime() {
        return quizStartTime;
    }

    public void setQuizStartTime(String quizStartTime) {
        this.quizStartTime = quizStartTime;
    }

    public String getQuizEndTime() {
        return quizEndTime;
    }

    public void setQuizEndTime(String quizEndTime) {
        this.quizEndTime = quizEndTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
