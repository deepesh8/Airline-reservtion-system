import java.sql.*;
import java.util.*;

class functions{
	static void booking(String uname){
		int i=1;
		Scanner sc1 = new Scanner(System.in);
		System.out.println("Enter the arrival destination");
		String arr = sc1.next();
		System.out.println("Enter the departure destination");
		String des = sc1.next();
		System.out.println("Enter the date in the format DD-MM-YYYY");
		String travel_date = sc1.next();
		System.out.println("Enter no of passengers");
		int no_pass = sc1.nextInt();
		int[] age=new int[no_pass];
		long[] contact=new long[no_pass];
		String[] name=new String[no_pass];
		for(int j=0;j<no_pass;j++)
		{
			System.out.println("Enter name of passenger "+(j+1));
			name[j] = sc1.next();
			System.out.println("Enter the age of passenger "+(j+1));
			age[j]= sc1.nextInt();
			System.out.println("Enter the contact details of passenger "+(j+1));
			contact[j] = sc1.nextLong();
		}
		try{
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			Statement s1 = conn.createStatement();
			ResultSet r1 = s1.executeQuery("select pnr from booking");
			while(r1.next()){
				if(r1.getInt("pnr")==i){
					i++;
				}
			}
			int temp=0;
			while(temp<no_pass)
			{
				s1.executeQuery("insert into booking values ('"+name[temp]+"',"+age[temp]+","+contact[temp]+",'"+des+"','"+arr+"',to_date('"+travel_date+"','DD-MM-YYYY'),"+i+",'"+uname+"')");
				temp=temp+1;
			}
			System.out.println("Enter further details");
			flight(uname,no_pass,name,contact);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	static void flight(String uname,int no_pass,String[] name,long contact[]){
		System.out.println("\nTHE AVAILABLE FLIGHTS AND SEATS ARE:- ");
		int i=1;
		int price=0;
		try{
			Scanner sc = new Scanner(System.in);
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			Statement stmt1 = conn.createStatement();
			ResultSet rset1 = stmt1.executeQuery("select * from flight");
			System.out.println("FLIGHT NUMBER \t\t SEATS AVAILABLE\t COST PER SEAT");
			while(rset1.next()){
				System.out.println(rset1.getString("flight_number")+"\t\t\t\t"+rset1.getString("seat_available")+"\t\t\t"+rset1.getString("cost_per_seat"));
			}
			System.out.println("\nENTER THE FLIGHT NUMBER");
			String flight = sc.next();
			flight+=sc.nextLine();
			System.out.println("S1-S75 is the LEFT MOST WINDOW SEATS");
			System.out.println("S76-S150 is the LEFT AISLE SEATS");
			System.out.println("S151-S225 is the RIGHT AISLE SEATS");
			System.out.println("S226-S300 is the RIGHT MOST WINDOW SEATS");
			int temp=0;
			Statement st1 = conn.createStatement();
			ResultSet r2 = st1.executeQuery("select pnr from seats");
			while(r2.next()){
				if(r2.getInt("pnr")==i){
					i++;
				}
			}
			while(temp<no_pass)
			{
				System.out.println("Enter the seat of passenger "+temp+1);
				String s1 = sc.next();
				ResultSet r1 = st1.executeQuery("select * from flight where flight_number='"+flight+"'"); 
				while(r1.next()){
					price = r1.getInt("cost_per_seat");
				}
				st1.executeQuery("insert into seats values('"+name[temp]+"',"+contact[temp]+",'"+flight+"','"+s1+"',"+price+","+i+",'"+uname+"')");
				st1.executeQuery("update flight set seat_available = seat_available-1 where flight_number = '"+flight+"'");
				temp = temp+1;
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("The total price would be "+price*no_pass+"\n");
		System.out.println("TICKETS HAVE BEEN BOOKED WITH CONFIRMED SEATS!");
	}
	static void change(String uname){
		Scanner sc = new Scanner(System.in);
		System.out.println("YOUR LAST FLIGHT DETAILS CAN BE CHANGED\n");
		System.out.println("1.CHANGE SEATS\n2.CHANGE CONTACTS\n3.CHANGE FLIGHT\n4.CHANGE PASSENGERS\n5.CHANGE DESTINATION AND ARRIVAL POINTS");
		int choice = sc.nextInt();
		switch(choice){
			case 1:
				try{
					Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
					Statement s1 = conn.createStatement();
					ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
					"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
					System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
					while(r1.next()){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+
						r1.getString("departure")+"\t\t"+r1.getString("arrival"));
					}
					int ch=0;
					do{
						System.out.println("Enter the contact number of the passenger for which the SEATS need to be changed");
						long con = sc.nextLong();
						System.out.println("S1-S75 is the LEFT MOST WINDOW SEATS");
						System.out.println("S76-S150 is the LEFT AISLE SEATS");
						System.out.println("S151-S225 is the RIGHT AISLE SEATS");
						System.out.println("S226-S300 is the RIGHT MOST WINDOW SEATS");
						System.out.println("Enter the seat to be changed into ");
						String seat = sc.next();
						s1.executeQuery("update seats set seat_no = '"+seat+"' where contact = "+con+" and acc='"+uname+"'"+
						"and pnr = (select max(pnr) from booking where acc='"+uname+"')");
						System.out.println("ENTER -1 TO STOP ELSE ANY NUMBER TO CONTINUE");
						ch = sc.nextInt();
					}while(ch!=-1);
				}catch(Exception e){
					System.out.println(e);
				}
				break;
			case 2:
				try{
					Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
					Statement s1 = conn.createStatement();
					ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
					"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
					System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
					while(r1.next()){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getString("departure")+"\t\t"+r1.getString("arrival"));
					}
					int ch;
					do{
						System.out.println("Enter the old contact number of the passenger for which needs to be changed");
						long con1 = sc.nextLong();
						System.out.println("Enter the new contact number of the passenger for which needs to be changed");
						long con2 = sc.nextLong();
						s1.executeQuery("update booking set contact = "+con2+" where contact = "+con1+" and acc = '"+uname+"'"+
						" and pnr = (select max(pnr) from booking where acc='"+uname+"')");
						s1.executeQuery("update seats set contact = "+con2+" where contact = "+con1+" and acc = '"+uname+"'"+
						" and pnr = (select max(pnr) from booking where acc='"+uname+"')");
						System.out.println("ENTER -1 TO STOP ELSE ANY NUMBER TO CONTINUE");
						ch = sc.nextInt();
					}while(ch!=-1);
				}catch(Exception e){
					System.out.println(e);
				}
				break;
			case 3:
				try{
					Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
					Statement s1 = conn.createStatement();
					ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
					"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
					System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
					String oldf="";
					while(r1.next()){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getString("departure")+"\t\t"+r1.getString("arrival"));
						oldf = r1.getString("flight_no");
					}
					int ch;
					do{
						System.out.println("\n");
						System.out.println("FLIGHT NUMBER \t\t SEATS AVAILABLE\t COST PER SEAT");
						Statement stmt1 = conn.createStatement();
						ResultSet rset1 = stmt1.executeQuery("select * from flight");
						while(rset1.next()){
							System.out.println(rset1.getString("flight_number")+"\t\t\t\t"+rset1.getString("seat_available")+"\t\t\t"
							+rset1.getString("cost_per_seat"));
						}
						System.out.println("Enter the new flight number to be changed to ");
						String newf = sc.next();
						newf+= sc.nextLine();
						stmt1.executeQuery("update seats set flight_no='"+newf +"' where flight_no = '"+oldf+"'" );
						System.out.println("Enter -1 to Exit");
						ch = sc.nextInt();
					}while(ch!=-1);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 4:
				try{
					Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
					Statement s1 = conn.createStatement();
					ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
					"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
					System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
					while(r1.next()){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getString("departure")+"\t\t"+r1.getString("arrival"));
					}
					int ch;
					do{
						System.out.println("Enter new passenger name");
						String newpas = sc.next();
						System.out.println("Enter the phone number of the old passenger");
						long con = sc.nextLong();
						System.out.println("Enter the contact details of the new passenger");
						long con1 = sc.nextLong();
						s1.executeQuery("update booking set name='"+newpas+"' where contact = "+con+" and acc = '"+uname+"'");
						s1.executeQuery("update seats set name='"+newpas+"' where contact = "+con+" and acc = '"+uname+"'");
						s1.executeQuery("update booking set contact = "+con1+" where name='"+newpas+"' and acc='"+uname+"'");
						s1.executeQuery("update seats set contact ="+con1+" where name ='"+newpas+"' and acc = '"+uname+"'");
						System.out.println("Enter -1 to stop");
						ch = sc.nextInt();
					}while(ch!=-1);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 5:
				try{
					String newarr ="";
					String newdep = "";
					String oldarr="";
					String olddep="";
					Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
					Statement s1 = conn.createStatement();
					ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
					"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
					System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
					while(r1.next()){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getString("departure")+"\t\t"+r1.getString("arrival"));
						oldarr = r1.getString("arrival");
						olddep = r1.getString("departure");
					}
					System.out.println("Enter new arr and dep places");
					newarr = sc.next();
					newdep = sc.next();
					s1.execute("update booking set arrival = '"+newarr+"' where arrival ='"+oldarr+"'"+" and acc = '"+uname+"'");
					s1.execute("update booking set departure = '"+newdep+"' where arrival ='"+newarr+"'"+" and acc = '"+uname+"'");
					System.out.println("UPDATES SUCCESSFULLY");
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}	
	}
	static void cancel(String uname){
		try{
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			Statement s1 = conn.createStatement();
			ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
			"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
			System.out.println("name\tcontact\t\tflight number\tseat number\tdeparture\tarrival");
			while(r1.next()){
				System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
				"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getString("departure")+"\t\t"+r1.getString("arrival"));
			}
			s1.executeQuery("delete from booking where acc='"+uname+"' and pnr =(select max(pnr) from booking where acc='"+uname+"')");
			s1.executeQuery("delete from seats where acc='"+uname+"' and pnr =(select max(pnr) from seats where acc='"+uname+"')");
			System.out.println("LAST FLIGHT REMOVED");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	static void itenary(String uname){
		try{
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			Statement s1 = conn.createStatement();
			ResultSet r1 = s1.executeQuery("select * from booking,seats where booking.pnr=(select max(pnr) from booking where acc='"+uname+
			"') and booking.contact=seats.contact and booking.pnr = seats.pnr");
			System.out.println("YOUR LAST FLIGHT WAS");
			System.out.println("NAME\tCONTACT\t\tFLIGHT NUMBER\tSEAT NUMBER\tTRAVEL DATE\tDEPARTURE\tARRIVAL\n");
			while(r1.next()){
				System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
				"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getDate("travel_date")+"\t"+r1.getString("departure")+
					"\t\t"+r1.getString("arrival"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	static void history(String uname){
		int i=1;
		System.out.println("YOUR PAST FLIGHTS WERE:-");
		try{
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			Statement st1 = conn.createStatement();
			if(uname.equals("admin")){
				ResultSet r1 = st1.executeQuery("select * from booking,seats where booking.contact=seats.contact and booking.pnr = seats.pnr order by seats.pnr asc");
				System.out.println("NAME\tCONTACT\t\tFLIGHT NUMBER\tSEAT NUMBER\tTRAVEL DATE\tDEPARTURE\tARRIVAL\tPNR\n");
				while(r1.next()){
					if(r1.getInt("pnr")==i){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getDate("travel_date")+"\t"+r1.getString("departure")+
						"\t\t"+r1.getString("arrival")+"\t"+r1.getInt("pnr"));
					}
					else{
						System.out.println("\n");
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getDate("travel_date")+"\t"+r1.getString("departure")+
						"\t\t"+r1.getString("arrival")+"\t"+r1.getInt("pnr"));
					}
				}
			}
			else{
				ResultSet r1 = st1.executeQuery("select * from booking,seats where booking.contact=seats.contact and booking.pnr = seats.pnr and booking.acc='"+uname+
				"' order by seats.pnr asc");
				System.out.println("NAME\tCONTACT\t\tFLIGHT NUMBER\tSEAT NUMBER\tTRAVEL DATE\tDEPARTURE\tARRIVAL\tPNR\n");
				while(r1.next()){
					if(r1.getInt("pnr")==i){
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getDate("travel_date")+"\t"+r1.getString("departure")+
						"\t\t"+r1.getString("arrival")+"\t"+r1.getInt("pnr"));
					}
					else{
						System.out.println("\n");
						System.out.println(r1.getString("name")+"\t"+r1.getLong("contact")+"\t"+r1.getString("flight_no")+
						"\t\t"+r1.getString("seat_no")+"\t\t"+r1.getDate("travel_date")+"\t"+r1.getString("departure")+
						"\t\t"+r1.getString("arrival")+"\t"+r1.getInt("pnr"));
					}
				}
			}

		}catch(Exception e){
			System.out.println(e);
		}
	}
}
public class proj {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String uname="";
		int choice;
		int ch1;
		try {
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","system","tiger");
			System.out.println("OJDBC driver loaded successfully");
			Statement s1 = conn.createStatement();
			int flag = 0;
			do{
				do{
					System.out.println("1.SIGN UP\n2.LOGIN\n3.EXIT");
					ch1= sc.nextInt();
					switch(ch1){
						case 1:
							System.out.println("Enter username");
							String name = sc.next();
							System.out.println("Enter password");
							String passkey = sc.next();
							Statement stmt = conn.createStatement();
							try{
								stmt.executeQuery("insert into login values ('"+name+"','"+passkey+"')");
								System.out.println("ACCOUNT CREATED ");
							}catch(Exception e){
								System.out.println("ERROR:USERNAME ALREADY EXITS");
							}
							break;
						case 2:
							System.out.println("Enter username");
							uname = sc.next();
							System.out.println("Enter password");
							String pwd = sc.next();
							ResultSet r1 = s1.executeQuery("select * from login where username='"+uname+"'and password='"+pwd+"'");
							int count = 0;
							while(r1.next()){
								++count;
							}
							if(count==1){
								System.out.println("LOGIN SUCCESSFUL, WELCOME "+uname);
								flag = 1;
								do{
									System.out.println("1.BOOK A FLIGHT\n2.CHANGE FLIGHT\n3.CANCEL FLIGHT\n4.GENERATE ITENARY\n5.VIEW PAST FLYING HISTORY\n6.EXIT");
									choice = 0;
									
									choice = sc.nextInt();
									
									switch(choice){
										case 1:
											System.out.println("Enter the booking details:");
											functions.booking(uname);
											break;
										case 2:
											System.out.println("Enter details to change flight");
											functions.change(uname);
											break;
										case 3:
											System.out.println("Enter details to cancel flight");
											functions.cancel(uname);
											break;
										case 4:
											functions.itenary(uname);
											break;
										case 5:
											functions.history(uname);
											break;
										case 6:
											System.out.println("THANK YOU FOR USING OUT SERVICES !");
											break;
										default:
											System.out.println("Enter a valid value");
											break;
									}
								}while(choice!=6);
							}
							else{
								System.out.println("ERROR");
							}
							break;
						case 3:
							System.out.println("WE HOPE YOU USE OUR SERVICES AGAIN");
					}
				}while(ch1!=3);
			}while(flag!=1 && ch1!=3);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}