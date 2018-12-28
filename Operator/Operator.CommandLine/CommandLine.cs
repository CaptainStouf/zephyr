using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Operator;
namespace Operator.CommandLine
{
    public class CommandLine
    {
        public static void Start()
        {
            
            Console.BackgroundColor = ConsoleColor.DarkBlue;
            Console.ForegroundColor = ConsoleColor.White;
            Console.Clear();
            bool firstLoop = true;

            while (true)
            {
                
                string messageText = String.Empty;
                Console.ForegroundColor = ConsoleColor.White;
                string title = "Operator";
                Console.Clear();
                if (true)
                {
                    Console.Write("Send an empty message to exit.\r\n");
                    firstLoop = false;
                }
                Console.Write("message >");
                messageText = Console.ReadLine();
                //Console.WriteLine($">[{DateTime.Now.ToShortTimeString()}] - {messageText}");
                if (String.IsNullOrWhiteSpace(messageText))
                    return;
                var message = new Message(new Payload(title, messageText));
                Console.ForegroundColor = ConsoleColor.DarkBlue;
                MessageSender.SendMessage(message);
                System.Threading.Thread.Sleep(100);
            }
        }

    }
}
