package com.ak47.digiboard.model;

import java.util.ArrayList;

public class QuizListModel {
    private String quizName, quizDescription, quizEncryptionCode;
    private Boolean publishInfo;
    private ArrayList<ExaminerQuestionListModel> questionsList;
    private String createdDateTime;

    public QuizListModel(String quizName, String quizDescription, String quizEncryptionCode, Boolean publishInfo, ArrayList<ExaminerQuestionListModel> questionsList, String createdDateTime) {
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.quizEncryptionCode = quizEncryptionCode;
        this.publishInfo = publishInfo;
        this.questionsList = questionsList;
        this.createdDateTime = createdDateTime;
    }

    public QuizListModel() {
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
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

    public String getQuizEncryptionCode() {
        return quizEncryptionCode;
    }

    public void setQuizEncryptionCode(String quizEncryptionCode) {
        this.quizEncryptionCode = quizEncryptionCode;
    }

    public Boolean getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(Boolean publishInfo) {
        this.publishInfo = publishInfo;
    }

    public ArrayList<ExaminerQuestionListModel> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<ExaminerQuestionListModel> questionsList) {
        this.questionsList = questionsList;
    }
}
