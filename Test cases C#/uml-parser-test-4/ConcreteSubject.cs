using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
   public class ConcreteSubject:Subject
    {
       private string subjectState;
       private ICollection<Observer> observers = new ArrayList<Observer>();

       public string getState()
       {
           return this.subjectState;
       }

       public void setState(string status)
       {
           subjectState = status;
           notifyObservers();
       }

       public void attach(Observer obj)
       {
           observers.Add(obj);
       }

       public void deattach(Observer obj)
       {
           observers.Remove(obj);
       }

       public void notifyObservers()
       {
           foreach (Observer obj in observers)
           {
               obj.update();
           }
       }

       public void showstate()
       {
           Console.WriteLine("Subject:" + this.GetType().Name + "  = " + subjectState);
       }


    }
}
