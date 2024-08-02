package com.example.googleformautoresponse;

public class Answer {
    String entity;
    float[] frequency;
    String[] answer;

    public float[] getFrequency() {
        return frequency;
    }

    public String[] getAnswer() {
        return answer;
    }

    public String getEntity() {
        return entity;
    }

    public void setAnswer(String[] answer) {
        this.answer = answer;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setFrequency(float[] frequency) {
        this.frequency = frequency;
    }

    public Answer(String entity, float[] frequency, String[] answers){
        setAnswer(answers);
        setEntity(entity);
        setFrequency(frequency);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ").append(entity).append("\n");
        sb.append("Frequency: ");
        if (frequency != null) {
            for (float f : frequency) {
                sb.append(f).append(" ");
            }
        }
        sb.append("\n");
        sb.append("Answers: ");
        if (answer != null) {
            for (String ans : answer) {
                sb.append(ans).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
