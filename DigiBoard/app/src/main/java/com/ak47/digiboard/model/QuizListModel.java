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

    public String getQuizName() {
        return quizName;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public String getQuizEncryptionCode() {
        return quizEncryptionCode;
    }

    public Boolean getPublishInfo() {
        return publishInfo;
    }

    public ArrayList<ExaminerQuestionListModel> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<ExaminerQuestionListModel> questionsList) {
        this.questionsList = questionsList;
    }
}
