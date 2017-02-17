using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
   public class ConcreteObserver:Observer
    {
       protected string observerState;
       protected ConcreteSubject subject;

       public ConcreteObserver(ConcreteSubject theSubject)
       {
           this.subject = theSubject;
       }

       public void update()
       {

       }
       public void showState()
       {
           Console.WriteLine("Observer : " + this.GetType().Name + " = " + observerState);
       }

    }
}
