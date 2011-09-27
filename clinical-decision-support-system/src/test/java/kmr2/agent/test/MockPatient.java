package kmr2.agent.test;

import mock.MockFact;

import java.io.Serializable;

public class MockPatient implements Serializable {

    private String patientId;

    private int age;

    public MockPatient(String patientId, int age) {
        this.patientId = patientId;
        this.age = age;
    }


    @Override
    public String toString() {
        return "Person{" +
                "patientId='" + patientId + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MockPatient person = (MockPatient) o;

        if (age != person.age) return false;
        if (patientId != null ? !patientId.equals(person.patientId) : person.patientId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = patientId != null ? patientId.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}