
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ReadersWritersSemaphore {

    static final Semaphore mutex = new Semaphore(1);
    static final Semaphore resourceLock = new Semaphore(1, true);

    static int readCount = 0;
    static int activeReaders = 0;
    static int activeWriters = 0;
    static int sharedValue = 0;

    static final TraceRecorder trace = new TraceRecorder();

    static class Event {
        long time;
        String actor, state, detail;
        int readers, value;

        Event(long time, String actor, String state,
              String detail, int readers, int value) {
            this.time = time;
            this.actor = actor;
            this.state = state;
            this.detail = detail;
            this.readers = readers;
            this.value = value;
        }

        String json() {
            return "{\"time\":" + time
                    + ",\"actor\":\"" + escape(actor)
                    + "\",\"state\":\"" + escape(state)
                    + "\",\"detail\":\"" + escape(detail)
                    + "\",\"readers\":" + readers
                    + ",\"value\":" + value + "}";
        }

        static String escape(String s) {
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
        }
    }

    static class TraceRecorder {
        final List<Event> events = new ArrayList<>();
        long startTime;

        synchronized void start() {
            startTime = System.nanoTime();
        }

        synchronized void record(
                String actor, String state, String detail) {
            long elapsed = (System.nanoTime() - startTime) / 1_000_000;
            events.add(new Event(elapsed, actor, state, detail,
                    activeReaders, sharedValue));
            System.out.printf("%5d ms | %-8s | %-8s | %s%n",
                    elapsed, actor, state, detail);
        }

        synchronized String toJson() {
            List<String> result = new ArrayList<>();
            for (Event e : events) result.add(e.json());
            return "[" + String.join(",", result) + "]";
        }

        synchronized long duration() {
            return events.isEmpty() ? 0
                    : events.get(events.size() - 1).time;
        }
    }

    static class Reader extends Thread {
        final Random random;

        Reader(int id) {
            super("R" + id);
            random = new Random(100 + id);
        }

        @Override
        public void run() {
            try {
                trace.record(getName(), "IDLE", "Ready");
                for (int round = 1; round <= 3; round++) {
                    Thread.sleep(150 + random.nextInt(200));

                    trace.record(getName(), "WAITING",
                            "Wants to read (round " + round + ")");

                    mutex.acquire();
                    readCount++;

                    if (readCount == 1) {
                        resourceLock.acquire();
                    }

                    activeReaders++;
                    trace.record(getName(), "READING",
                            "Entered critical section; readers inside = "
                                    + activeReaders);
                    mutex.release();

                    try {
                        Thread.sleep(450 + random.nextInt(250));
                        int seen;
                        synchronized (ReadersWritersSemaphore.class) {
                            seen = sharedValue;
                        }

                        trace.record(getName(), "READING",
                                "Reading shared value " + seen);
                    } finally {
                        mutex.acquire();
                        activeReaders--;
                        readCount--;

                        trace.record(getName(), "IDLE",
                                "Finished reading; readers left inside = "
                                        + activeReaders);

                        if (readCount == 0) {
                            resourceLock.release();
                        }
                        mutex.release();
                    }
                }

                trace.record(getName(), "IDLE", "All rounds completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Writer extends Thread {
        final Random random;

        Writer(int id) {
            super("W" + id);
            random = new Random(200 + id);
        }

        @Override
        public void run() {
            try {
                trace.record(getName(), "IDLE", "Ready");
                for (int round = 1; round <= 3; round++) {
                    Thread.sleep(250 + random.nextInt(250));

                    trace.record(getName(), "WAITING",
                            "Wants to write (round " + round + ")");

                    resourceLock.acquire();

                    activeWriters++;
                    trace.record(getName(), "WRITING",
                            "Entered critical section (exclusive access)");

                    try {
                        Thread.sleep(500 + random.nextInt(200));

                        int newValue;
                        synchronized (ReadersWritersSemaphore.class) {
                            newValue = ++sharedValue;
                        }

                        trace.record(getName(), "WRITING",
                                "Updated shared value to " + newValue);
                    } finally {
                        activeWriters--;
                        trace.record(getName(), "IDLE",
                                "Finished writing; resource released");
                        resourceLock.release();
                    }
                }

                trace.record(getName(), "IDLE", "All rounds completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void generateHtml() throws IOException {
        Path output = Path.of("generated_html",
                "readers_writers_semaphore.html");
        Files.createDirectories(output.getParent());

        String html = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Readers-Writers - Semaphore Solution</title>
<style>
*{box-sizing:border-box}
body{margin:0;background:#f4f6fa;color:#172033;
font-family:Arial,sans-serif;font-size:13px}
main{max-width:1250px;margin:auto;padding:12px 14px}
h1{font-size:19px;margin:0 0 3px}
.subtitle{color:#65748b;font-size:11px;margin-bottom:12px}
.panel{background:white;border:1px solid #d7dfeb;border-radius:10px;
padding:11px;margin:10px 0}
.controls{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
button,select{background:white;border:1px solid #d4dce8;border-radius:7px;
padding:7px 10px;color:#27354a;cursor:pointer}
button:hover{background:#edf4ff}
input[type=range]{accent-color:#087cff}
.legend{display:flex;gap:9px;margin-top:8px;color:#718096;font-size:10px}
.dot{display:inline-block;width:9px;height:9px;border-radius:2px;margin-right:3px}
.idle{background:#9aa6b5}.waiting{background:#f59e0b}
.reading{background:#21a66b}.writing{background:#ed4b54}
.layout{display:grid;grid-template-columns:1fr 1.2fr 1fr;gap:10px;min-height:225px}
.heading{font-size:11px;font-weight:bold;color:#64748b;margin-bottom:7px}
.actor{border:2px solid #cbd5e1;border-radius:9px;padding:7px 9px;
margin:6px 0;display:flex;align-items:center;justify-content:space-between;
font-size:13px;font-weight:bold;background:#fff}
.actor .badge{font-size:10px;border-radius:12px;color:white;
padding:3px 7px}
.actor.IDLE{border-color:#a7b1c0}
.actor.IDLE .badge{background:#9aa6b5}
.actor.WAITING{border-color:#f59e0b}
.actor.WAITING .badge{background:#f59e0b}
.actor.READING{border-color:#21a66b}
.actor.READING .badge{background:#21a66b}
.actor.WRITING{border-color:#ed4b54}
.actor.WRITING .badge{background:#ed4b54}
.resource{border:2px solid #21a66b;border-radius:10px;min-height:110px;
display:flex;flex-direction:column;align-items:center;justify-content:center;
text-align:center;padding:12px}
.resource.busy{border-color:#ed4b54}
.resource .number{font-size:30px;font-weight:bold}
.good{color:#21a66b;font-weight:bold;margin-top:4px}
.bad{color:#ed4b54;font-weight:bold;margin-top:4px}
.small{font-size:11px;color:#718096;margin-top:6px;line-height:1.5}
.timeline-row{display:grid;grid-template-columns:30px 1fr;gap:8px;
align-items:center;margin:7px 0}
.lane{height:14px;position:relative;background:#f0f3f8;border-radius:3px;
overflow:hidden}
.segment{position:absolute;height:100%}
.cursor{position:absolute;top:-3px;height:20px;width:2px;background:#087cff;
z-index:3}
.eventlog{height:175px;overflow:auto;border:1px solid #d8e0ec;border-radius:7px;
font-family:Consolas,monospace;font-size:10px}
.logrow{padding:2px 5px;white-space:nowrap;cursor:pointer;color:#8a96a7}
.logrow.selected{background:#4b7cff;color:white}
.logrow:hover{background:#e7efff;color:#172033}
@media(max-width:700px){main{padding:8px}.layout{grid-template-columns:1fr}
.resource{min-height:120px}.controls{gap:5px}}
</style>
</head>
<body>
<main>
<h1>Readers-Writers - Semaphore Solution</h1>
<div class="subtitle">Semaphores: mutex(1), resourceLock(1) |
4 readers, 2 writers — generated from the execution trace of the Java program
(<span id="eventCount"></span> events).</div>

<div class="panel">
<div class="controls">
<button onclick="play()" id="playButton">▶ Play</button>
<button onclick="step()">Step ▶|</button>
<button onclick="reset()">↺ Reset</button>
<label>Speed
<select id="speed">
<option value="0.5">0.5x</option>
<option value="1" selected>1x</option>
<option value="2">2x</option>
<option value="4">4x</option>
</select></label>
<input id="slider" type="range" min="0" max="1000" value="0"
style="flex:1;min-width:100px">
<span id="clock" style="color:#718096;white-space:nowrap"></span>
</div>
<div class="legend">
<span><i class="dot idle"></i>IDLE</span>
<span><i class="dot waiting"></i>WAITING</span>
<span><i class="dot reading"></i>READING</span>
<span><i class="dot writing"></i>WRITING</span>
</div>
</div>

<div class="panel layout">
<section><div class="heading">READERS</div><div id="readers"></div></section>
<section>
<div class="heading">SHARED RESOURCE (critical section)</div>
<div class="resource" id="resource">
<div>counter value</div><div class="number" id="value">0</div>
<div class="good" id="access">SHARED READ</div>
</div>
<div class="small" id="safety"></div>
<div class="small" id="queue"></div>
<div class="small" id="entries"></div>
</section>
<section><div class="heading">WRITERS</div><div id="writers"></div></section>
</div>

<div class="panel">
<b>Timeline</b> <span class="small">(one lane per thread; blue line = current time)</span>
<div id="timeline"></div>
</div>

<div class="panel">
<b>Event log</b> <span class="small">(click a line to jump to it)</span>
<div id="log" class="eventlog"></div>
</div>
</main>

<script>
const TRACE = __TRACE__;
const actors = ["R1","R2","R3","R4","W1","W2"];
const colors = {IDLE:"#9aa6b5",WAITING:"#f59e0b",
                READING:"#21a66b",WRITING:"#ed4b54"};
const endTime = Math.max(1, ...TRACE.map(e=>e.time));
let current=0, timer=null;

document.getElementById("eventCount").textContent=TRACE.length;
document.getElementById("slider").max=endTime;

function stateAt(actor,t){
  let found=null;
  for(const e of TRACE){
    if(e.actor===actor && e.time<=t) found=e;
  }
  return found;
}

function render(){
  const t=Number(document.getElementById("slider").value);
  current=t;
  const ev=[...TRACE].reverse().find(e=>e.time<=t);
  document.getElementById("clock").textContent=
    Math.round(t)+" / "+endTime+" ms";

  document.getElementById("readers").innerHTML=actors.slice(0,4).map(a=>{
    const e=stateAt(a,t), s=e?e.state:"IDLE";
    return '<div class="actor '+s+'"><span>'+a+'</span>'+
      '<span class="badge">'+s+'</span></div>';
  }).join("");

  document.getElementById("writers").innerHTML=actors.slice(4).map(a=>{
    const e=stateAt(a,t), s=e?e.state:"IDLE";
    return '<div class="actor '+s+'"><span>'+a+'</span>'+
      '<span class="badge">'+s+'</span></div>';
  }).join("");

  const lastValue=ev?ev.value:0;
  document.getElementById("value").textContent=lastValue;

  const states=actors.map(a=>[a,stateAt(a,t)]);
  const reading=states.filter(([a,e])=>e&&e.state==="READING").map(([a])=>a);
  const writing=states.filter(([a,e])=>e&&e.state==="WRITING").map(([a])=>a);

  const violation=reading.length>0 && writing.length>0 || writing.length>1;
  const res=document.getElementById("resource");
  res.classList.toggle("busy",writing.length>0);
  document.getElementById("access").className=violation?"bad":"good";
  document.getElementById("access").textContent=violation?
    "SAFETY VIOLATION":writing.length?
    "EXCLUSIVE WRITE by "+writing.join(", "):
    reading.length?"SHARED READ by "+reading.join(", "):"RESOURCE AVAILABLE";

  document.getElementById("safety").textContent=
    "Safety check: "+(violation?"OVERLAP DETECTED":"no reader/writer or writer/writer overlap in displayed states");
  document.getElementById("queue").textContent=
    "Waiting queue: "+states.filter(([a,e])=>e&&e.state==="WAITING")
      .map(([a])=>a).join(", ");
  document.getElementById("entries").textContent=
    "Critical-section entries so far: "+
    TRACE.filter(e=>e.time<=t && (e.state==="READING"||e.state==="WRITING")).length;

  document.getElementById("timeline").innerHTML=actors.map(a=>{
    const evs=TRACE.filter(e=>e.actor===a);
    let segs="";
    for(let i=0;i<evs.length;i++){
      const start=evs[i].time;
      const end=i+1<evs.length?evs[i+1].time:endTime;
      if(end<=start)continue;
      segs+='<span class="segment" title="'+evs[i].state+
        '" style="left:'+(start/endTime*100)+'%;width:'+
        ((end-start)/endTime*100)+'%;background:'+
        colors[evs[i].state]+'"></span>';
    }
    return '<div class="timeline-row"><span>'+a+'</span><div class="lane">'+
      segs+'<span class="cursor" style="left:'+(t/endTime*100)+'%"></span>'+
      '</div></div>';
  }).join("");

  const selectedIndex=TRACE.findLastIndex(e=>e.time<=t);
  document.querySelectorAll(".logrow").forEach((row,i)=>{
    row.classList.toggle("selected",i===selectedIndex);
  });
}

document.getElementById("log").innerHTML=TRACE.map((e,i)=>
  '<div class="logrow" onclick="jump('+i+')" data-i="'+i+'">[ '+
  String(e.time).padStart(4," ")+' ms] '+e.actor+' '+e.state+
  '  '+e.detail.replace(/[&<>]/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;"}[c]))+
  '</div>').join("");

function jump(i){
  current=TRACE[i].time;
  document.getElementById("slider").value=current;
  render();
  const row=document.querySelector('[data-i="'+i+'"]');
  if(row)row.scrollIntoView({block:"nearest"});
}
function step(){
  pause();
  const next=TRACE.find(e=>e.time>current);
  document.getElementById("slider").value=next?next.time:endTime;
  render();
}
function play(){
  if(timer){pause();return;}
  document.getElementById("playButton").textContent="⏸ Pause";
  const tick=()=>{
    const speed=Number(document.getElementById("speed").value);
    let next=current+30*speed;
    if(next>=endTime){next=endTime;pause();}
    document.getElementById("slider").value=next;
    render();
  };
  timer=setInterval(tick,100);
}
function pause(){
  if(timer){clearInterval(timer);timer=null;}
  document.getElementById("playButton").textContent="▶ Play";
}
function reset(){
  pause();
  document.getElementById("slider").value=0;
  render();
}
document.getElementById("slider").addEventListener("input",()=>{
  pause();render();
});
render();
</script>
</body>
</html>
""".replace("__TRACE__", trace.toJson());

        Files.writeString(output, html, StandardCharsets.UTF_8);
        System.out.println("\nHTML generated: " + output.toAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
        trace.start();

        System.out.println("Readers-Writers - Semaphore Solution");
        Thread[] threads = new Thread[6];

        for (int i = 0; i < 4; i++) threads[i] = new Reader(i + 1);
        for (int i = 0; i < 2; i++) threads[4 + i] = new Writer(i + 1);

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("\nFinal shared value: " + sharedValue
                + " (expected 6)");

        if (activeReaders != 0 || activeWriters != 0) {
            throw new IllegalStateException("Resource still occupied!");
        }

        generateHtml();
    }
}
