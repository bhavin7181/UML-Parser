using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
    
    public class Optimist:ConcreteObserver
    {
        public Optimist(ConcreteSubject sub):base(sub)
        {
            
        }
        public void update()
        {
            if (String.Compare(subject.getState, "The price of gas is $5.00/gal", true) == 0)
            {
                observerState = "Great! It's time to go green";
            }
            else if(String.Compare(subject.getState,"The new ipad is out today",true)==0)
            {
                observerState = "Apple take my money!";
            }
            else
            {
                observerState = ":)";
            }
        }

    }
}
