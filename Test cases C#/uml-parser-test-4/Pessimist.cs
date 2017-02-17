using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
    public class Pessimist:ConcreteObserver
    {
         public Pessimist(ConcreteSubject sub):base(sub)
        {
            
        }
        public void update()
        {
            if (String.Compare(subject.getState, "The price of gas is $5.00/gal", true) == 0)
            {
                observerState = "This is the beginning of the end of the world";
            }
            else if(String.Compare(subject.getState,"The new ipad is out today",true)==0)
            {
                observerState = "Not another ipad!";
            }
            else
            {
                observerState = ":(";
            }
        }
    }
}
