import java.util.Scanner;
class Grosssalary
{
Double BS,TA,DA,HRA,Gross;

void getdata()
{
Scanner Sc=new Scanner(System.in);
BS=Sc.nextInt();
}
void show()
{
DA=BS*40/100;
TA=BS*20/100;
HRA=BS*10/100;
Gross=BS+DA+TA+HRA;
System.out.print(Gross);
}
public static void main(String args[])
{
Groossalary p=new Groossalary();
p.getdata();
p.show();
}
}
