package kh.student;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class StudentMain {
	public static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		boolean exit = false;
		int menu = 0;

		while (!exit) {
			try {
				// 초기화면 메뉴 출력
				System.out.println("***************");
				System.out.println("1.학생로그인 \n2.관리자로그인");
				System.out.println("***************");

				menu = sc.nextInt();

				if (menu < 1 || menu > 2) {
					System.out.println("제대로 입력해주세요");
				} else {
					switch (menu) {
					case 1:
						menuChoiceStudent();
						exit = true;
						break;
					case 2:
						menuChoiceAdmin();
						exit = true;
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("잘못된 선택입니다.");
				exit = true;
			}

		}

	}

	public static void menuChoiceStudent() {
		System.out.println("아이디와 비밀번호를 입력하세요");

		sc.nextLine(); // 버퍼
		System.out.print("아이디 : ");
		String sd_id = sc.nextLine();

		System.out.print("패스워드 : ");
		String sd_pd = sc.nextLine();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "select name from student where sd_id = ? and sd_pd = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, sd_id);
			pstmt.setString(2, sd_pd);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				System.out.println(name + "님 환영합니다.");
				boolean quit = false;
				int numberSelection = 0;
				while (!quit) {
					menuIntroduction2();
					System.out.print("메뉴 번호를 선택해주세요>>");
					numberSelection = sc.nextInt();
					sc.nextLine(); // 입력버퍼 클리어

					if (numberSelection < 1 || numberSelection > 3) {
						System.out.println("1부터 3까지의 숫자를 입력하세요.");
					} else {
						switch (numberSelection) {
						case 1: // 내정보보기
							selectMyInfo();
							break;
						case 2: // 내정보 수정하기
							updateMyInfo();
							break;
						case 3: // 종료하기
							System.out.println("종료하겠습니다.");
							quit = true;
							break;
						}
					}
				} // end of while
				System.out.println("The end");
			} else {
				System.out.println("로그인에 실패했습니다.");
				System.out.println("종료합니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}

	}

	public static void updateMyInfo() {
		Student student = selectMyInfo();
		System.out.println("변경될 이름(현재 : " + student.getName() + ")>>");
		String name = sc.nextLine();
		System.out.println("변경될 생일(현재 : " + student.getBirthday() + ")>>");
		String birthday = sc.nextLine();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "update student set name = ?, birthday = ? where name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, birthday);
			pstmt.setString(3, student.getName());

			int i = pstmt.executeUpdate();
			if (i == 1) {
				System.out.println(name + "내 정보를 수정 했습니다.");
			} else {
				System.out.println(name + "내 정보를 수정하는데 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("pstmt, con close error");
				e.printStackTrace();
			}
		}
	}

	public static Student selectMyInfo() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		System.out.println("본인확인을 위해 비밀번호를 다시 입력해주세요>>");
		String sd_pd = sc.nextLine();
		Student student = null;
		try {
			
			con = SQLStudentConnect.makeConnection();
			String sql = "select student_no,name,birthday from student where sd_pd = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, sd_pd);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int student_no = rs.getInt("student_no");
				String name = rs.getString("name");
				String birthday = rs.getString("birthday");
				student = new Student(student_no, name, birthday);
				System.out.println("[" + student_no + ",\t" + name + ",\t" + birthday + "]");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}
		return student;
	}

	public static void menuChoiceAdmin() throws FileNotFoundException, IOException {
		System.out.println("관리자 정보를 입력하세요");

		sc.nextLine(); // 버퍼
		System.out.print("아이디 : ");
		String adminId = sc.nextLine();

		System.out.print("패스워드 : ");
		String adminPW = sc.nextLine();

		// 어드민정보 파일불러오기

		String filePath = "C:/Users/user/eclipse-workspace/dbhomework/src/kh/student/admin.properties";
		Properties properties = new Properties();
		properties.load(new FileReader(filePath));
		String _adminid = properties.getProperty("id");
		String _adminpw = properties.getProperty("pw");

		if (adminId.equals(_adminid) && adminPW.equals(_adminpw)) {
			System.out.println("로그인 성공");
			boolean quit = false;
			int numberSelection = 0;
			while (!quit) {
				menuIntroduction();
				System.out.print("메뉴 번호를 선택해주세요>>");
				numberSelection = sc.nextInt();
				sc.nextLine(); // 입력버퍼 클리어

				if (numberSelection < 1 || numberSelection > 6) {
					System.out.println("1부터 6까지의 숫자를 입력하세요.");
				} else {
					switch (numberSelection) {
					case 1: // 학생정보보기
						selectAllStudent();
						break;
					case 2: // 학생정보입력하기
						Student student = inputStudent();
						insertStudent(student);
						break;
					case 3: // 학생정보검색하기
						searchStudent();
						break;
					case 4: // 학생정보수정하기
						updateStudent();
						break;
					case 5: // 학생정보삭제하기
						deleteStudent();
						break;
					case 6: // 종료하기
						System.out.println("종료하겠습니다.");
						quit = true;
						break;
					}
				}
			} // end of while
			System.out.println("The end");
		} else {
			System.out.println("관리자 로그인 실패");
		}

	}

	public static void deleteStudent() {
		selectAllStudent();
		System.out.println("삭제하고자 하는 학생의 이름 입력>>");
		String name = sc.nextLine();

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "delete from student where name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);

			int i = pstmt.executeUpdate();
			if (i == 1) {
				System.out.println(name + "학생의 정보가 삭제되었습니다.");
			} else {
				System.out.println(name + "학생의 정보 삭제에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}
	}

	private static void updateStudent() {
		Student student = searchStudent();

		if (student == null) {
			System.out.println("학생정보를 찾지못함");
		} else {
			System.out.println("변경될 이름(현재 : " + student.getName() + ")>>");
			String name = sc.nextLine();
			System.out.println("변경될 생일(현재 : " + student.getBirthday() + ")>>");
			String birthday = sc.nextLine();
			System.out.println("변경될 아이디(현재 : " + student.getSd_id() + ")>>");
			String sd_id = sc.nextLine();
			System.out.println("변경될 비밀번호(현재 : " + student.getSd_pd() + ")>>");
			String sd_pd = sc.nextLine();

			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = SQLStudentConnect.makeConnection();
				String sql = "update student set name = ?, birthday = ?, sd_id =?, sd_pd=? where name = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, birthday);
				pstmt.setString(3, sd_id);
				pstmt.setString(4, sd_pd);
				pstmt.setString(5, student.getName());

				int i = pstmt.executeUpdate();
				if (i == 1) {
					System.out.println(name + "학생의 정보를 수정 했습니다.");
				} else {
					System.out.println(name + "학생의 정보를 수정하는데 실패했습니다.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null) {
						pstmt.close();
					}
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					System.out.println("pstmt, con close error");
					e.printStackTrace();
				}
			}
		}
	}

	public static Student searchStudent() {
		System.out.print("찾을 학생의 이름 입력>>");
		String name = sc.nextLine();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Student student = null;
		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "select * from student where name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int student_no = rs.getInt("student_no");
				String _name = rs.getString("name");
				String birthday = rs.getString("birthday");
				String sd_id = rs.getString("sd_id");
				String sd_pd = rs.getString("sd_pd");

				student = new Student(student_no, _name, birthday, sd_id, sd_pd);
				System.out.println(student.toString());
			} else {
				System.out.println(name + " 학생의 정보가 없습니다.");
				student = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}
		return student;
	}

	private static Student inputStudent() {
		System.out.print("학생 이름입력>>");
		String name = sc.nextLine();
		System.out.print("학생 생년월일 입력 ex)yyyymmdd>>");
		String birthday = sc.nextLine();
		System.out.print("학생 아이디 입력>>");
		String sd_id = sc.nextLine();
		System.out.print("학생 비밀번호 입력>>");
		String sd_pd = sc.nextLine();

		Student student = new Student(0, name, birthday, sd_id, sd_pd);
		return student;
	}

	public static void insertStudent(Student student) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "INSERT INTO student values (num_seq.nextval,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, student.getName());
			pstmt.setString(2, student.getBirthday());
			pstmt.setString(3, student.getSd_id());
			pstmt.setString(4, student.getSd_pd());
			int i = pstmt.executeUpdate();
			if (i == 1) {
				System.out.println(student.getName() + "학생의 정보가 입력되었습니다.");
			} else {
				System.out.println(student.getName() + "학생의 정보 입력에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}
	}

	public static void selectAllStudent() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = SQLStudentConnect.makeConnection();
			String sql = "select * from student order by student_no";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int student_no = rs.getInt("student_no");
				String name = rs.getString("name");
				String birthday = rs.getString("birthday");
				String sd_id = rs.getString("sd_id");
				String sd_pd = rs.getString("sd_pd");

				Student student = new Student(student_no, name, birthday, sd_id, sd_pd);
				System.out.println(student.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("rs, pstmt, con close error");
				e.printStackTrace();
			}
		}
	}

	public static void menuIntroduction() {
		System.out.println("****************************************");
		System.out.println("\tAdmin");
		System.out.println("****************************************");
		System.out.println("1. 학생정보 보기");
		System.out.println("2. 학생정보 입력하기");
		System.out.println("3. 학생정보 조회하기");
		System.out.println("4. 학생정보 수정하기");
		System.out.println("5. 학생정보 삭제하기");
		System.out.println("6. 종료하기");
		System.out.println("****************************************");

	}
	public static void menuIntroduction2() {
		System.out.println("****************************************");
		System.out.println("\tStudent Information");
		System.out.println("****************************************");
		System.out.println("1. 내정보 보기");
		System.out.println("2. 내정보 수정하기");
		System.out.println("3. 종료하기");
		System.out.println("****************************************");
		
	}

}
