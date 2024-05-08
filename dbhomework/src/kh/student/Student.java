package kh.student;

public class Student {
	private int student_no;
	private String name;
	private String birthday;
	private String sd_id;
	private String sd_pd;
	public Student() {
		super();
	}
	
	public Student(int student_no, String name, String birthday) {
		super();
		this.student_no = student_no;
		this.name = name;
		this.birthday = birthday;
	}
	public Student(int student_no, String name, String birthday, String sd_id, String sd_pd) {
		super();
		this.student_no = student_no;
		this.name = name;
		this.birthday = birthday;
		this.sd_id = sd_id;
		this.sd_pd = sd_pd;
	}
	public int getStudent_no() {
		return student_no;
	}
	public void setStudent_no(int student_no) {
		this.student_no = student_no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSd_id() {
		return sd_id;
	}
	public void setSd_id(String sd_id) {
		this.sd_id = sd_id;
	}
	public String getSd_pd() {
		return sd_pd;
	}
	public void setSd_pd(String sd_pd) {
		this.sd_pd = sd_pd;
	}
	@Override
	public String toString() {
		return "[" + student_no + ",\t" + name + ",\t" + birthday + ",\t" + sd_id
				+ ",\t" + sd_pd + "]";
	}
	
	
	
}
