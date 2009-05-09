package Temp;

public class Label
{
  private String     name;
  private static int count;

  public String toString()
  {
    return name;
  }

  public Label(String n)
  {
    name = n;
  }

  public Label()
  {
    this("L" + count++);
  }
}
