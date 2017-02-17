using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
    interface Subject
    {
        void attach(Observer obj);
        void deattach(Observer obj);
        void notifyObservers();
    }
}
