using Quobject.SocketIoClientDotNet.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
namespace Operator
{
    public class MessageSender
    {
        public static void SendMessage(Message message)
        {
            var socket = IO.Socket("http://localhost:3753");
            var jsonString = JsonConvert.SerializeObject(message);
            try
            {
                var x = socket.Emit("notification", jsonString);
            }
            catch { }
            
        }
    }
}
