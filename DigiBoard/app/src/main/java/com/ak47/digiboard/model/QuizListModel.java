package com.ak47.digiboard.model;

import java.util.ArrayList;

public class QuizListModel {
    private String quizName, quizDescription;
    private Boolean publishInfo;
    private ArrayList<QuestionListModel> questionsList;
    private String createdDateTime;

    public QuizListModel(String quizName, String quizDescription, Boolean publishInfo, ArrayList<QuestionListModel> questionsList, String createdDateTime) {
        this.quizName = quizName;
        this.quizDescription = quizDescription;
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
    public Boolean getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(Boolean publishInfo) {
        this.publishInfo = publishInfo;
    }

    public ArrayList<QuestionListModel> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<QuestionListModel> questionsList) {
        this.questionsList = questionsList;
    }
}
