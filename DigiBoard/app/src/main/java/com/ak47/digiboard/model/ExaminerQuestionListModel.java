package com.ak47.digiboard.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ExaminerQuestionListModel implements Parcelable {
    public static final Creator<ExaminerQuestionListModel> CREATOR = new Creator<ExaminerQuestionListModel>() {
        @Override
        public ExaminerQuestionListModel createFromParcel(Parcel source) {
            return new ExaminerQuestionListModel(source);
        }

        @Override
        public ExaminerQuestionListModel[] newArray(int size) {
            return new ExaminerQuestionListModel[size];
        }
    };
    private String question, optionA, optionB, optionC, optionD;
    private int ans;

    private ExaminerQuestionListModel(Parcel source) {
        this.question = source.readString();
        this.optionA = source.readString();
        this.optionB = source.readString();
        this.optionC = source.readString();
        this.optionD = source.readString();
        this.ans = source.readInt();
    }

    public ExaminerQuestionListModel(String question, String optionA, String optionB, String optionC, String optionD, int ans) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.ans = ans;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getAnswer() {
        return ans;
    }

    public void setAnswer(int ans) {
        this.ans = ans;
    }

    // Override parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question);
        dest.writeString(this.optionA);
        dest.writeString(this.optionB);
        dest.writeString(this.optionC);
        dest.writeString(this.optionD);
        dest.writeInt(this.ans);
    }

}
