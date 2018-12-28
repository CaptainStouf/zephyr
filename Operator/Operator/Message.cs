using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Quobject.SocketIoClientDotNet.Client;

using Newtonsoft.Json;

namespace Operator
{
    [JsonObject("")]
    public class Message
    {
        public Message()
        {
            MetaData = new MetaData()
            {
                version = 1,
                type = "notification",
                from = "client",
                to = ""
            };
        }

        public Message(Payload payload) : this()
        {
            Payload = payload;
        }

        [JsonProperty("metadata")]
        internal MetaData MetaData { get; set; }

        [JsonProperty("payload")]
        public Payload Payload { get; set; }
        
    }
    
}
