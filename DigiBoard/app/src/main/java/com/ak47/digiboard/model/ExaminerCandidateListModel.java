package com.ak47.digiboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class ExaminerCandidateListModel implements Parcelable {
    public static final Creator<ExaminerCandidateListModel> CREATOR = new Creator<ExaminerCandidateListModel>() {
        @Override
        public ExaminerCandidateListModel createFromParcel(Parcel in) {
            return new ExaminerCandidateListModel(in);
        }

        @Override
        public ExaminerCandidateListModel[] newArray(int size) {
            return new ExaminerCandidateListModel[size];
        }
    };
    private String name, email, profilePic;

    public ExaminerCandidateListModel() {
    }


    public ExaminerCandidateListModel(String name, String email, String profilePic) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
    }

    private ExaminerCandidateListModel(Parcel in) {
        name = in.readString();
        email = in.readString();
        profilePic = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profilePic);
        dest.writeString(this.email);
    }


    @NotNull
    @Override
    public String toString() {
        return "ExaminerCandidateListModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }

}
