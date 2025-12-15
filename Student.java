class Student
{
int id;
String name;
Student(int k,String n)
{
this.id=k;
this.name=n;
}
void disp()
{
System.out.print(id+" "+name);
}
}
class X
{
public static void main(String args[])
{
Student p=new Student(123,"AURO");
p.disp();
}
}