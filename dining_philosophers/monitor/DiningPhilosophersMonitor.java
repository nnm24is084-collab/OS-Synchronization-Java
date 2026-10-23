
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiningPhilosophersMonitor {

    static final int N = 4;
    static final int MEALS = 3;

    enum State {
        THINKING, HUNGRY, EATING
    }

    static class Event {
        final int time;
        final int philosopher;
        final String action;
        final String message;
        final String[] states;
        final int[] meals;
        final int[] forks;

        Event(int time, int philosopher, String action, String message,
              State[] states, int[] meals, int[] forks) {
            this.time = time;
            this.philosopher = philosopher;
            this.action = action;
            this.message = message;
            this.states = new String[N];

            for (int i = 0; i < N; i++) {
                this.states[i] = states[i].name();
            }

            this.meals = meals.clone();
            this.forks = forks.clone();
        }
    }

    static class DiningMonitor {
        private final State[] state = new State[N];
        private final int[] meals = new int[N];
        private final int[] forks = new int[N];
        private final List<Event> events = new ArrayList<>();
        private int clock = 0;

        DiningMonitor() {
            for (int i = 0; i < N; i++) {
                state[i] = State.THINKING;
                forks[i] = -1;
            }
            record(-1, "START", "Simulation started");
        }

        private int left(int p) {
            return (p + N - 1) % N;
        }

        private int right(int p) {
            return (p + 1) % N;
        }

        private void record(int p, String action, String message) {
            events.add(new Event(
                clock++, p, action, message, state, meals, forks
            ));
        }

        private void test(int p) {
            if (state[p] == State.HUNGRY
                    && state[left(p)] != State.EATING
                    && state[right(p)] != State.EATING) {

                state[p] = State.EATING;
                forks[p] = p;
                forks[right(p)] = p;

                record(p, "EAT", "P" + p + " picked up forks "
                    + p + " and " + right(p) + " and started eating");
                notifyAll();
            }
        }

        synchronized void takeForks(int p) throws InterruptedException {
            state[p] = State.HUNGRY;
            record(p, "HUNGRY", "P" + p + " is hungry");

            test(p);

            while (state[p] != State.EATING) {
                wait();
            }
        }

        synchronized void putForks(int p) {
            state[p] = State.THINKING;
            meals[p]++;

            forks[p] = -1;
            forks[right(p)] = -1;

            record(p, "THINK", "P" + p + " finished meal "
                + meals[p] + " and released both forks");

            test(left(p));
            test(right(p));
            notifyAll();
        }

        synchronized void think(int p) {
            record(p, "THINK", "P" + p + " is thinking");
        }

        synchronized List<Event> getEvents() {
            return new ArrayList<>(events);
        }

        synchronized int[] getMeals() {
            return meals.clone();
        }
    }

    static final DiningMonitor monitor = new DiningMonitor();

    static class Philosopher extends Thread {
        private final int id;

        Philosopher(int id) {
            this.id = id;
            setName("P" + id);
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < MEALS; i++) {
                    monitor.think(id);
                    Thread.sleep(200 + id * 50L);

                    monitor.takeForks(id);
                    Thread.sleep(300 + id * 40L);

                    monitor.putForks(id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String buildJson(List<Event> events) {
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            if (i > 0) out.append(",");

            out.append("{\"time\":").append(e.time)
               .append(",\"p\":").append(e.philosopher)
               .append(",\"action\":\"").append(esc(e.action))
               .append("\",\"message\":\"").append(esc(e.message))
               .append("\",\"states\":[");

            for (int j = 0; j < N; j++) {
                if (j > 0) out.append(",");
                out.append("\"").append(e.states[j]).append("\"");
            }

            out.append("],\"meals\":[");
            for (int j = 0; j < N; j++) {
                if (j > 0) out.append(",");
                out.append(e.meals[j]);
            }

            out.append("],\"forks\":[");
            for (int j = 0; j < N; j++) {
                if (j > 0) out.append(",");
                out.append(e.forks[j]);
            }
            out.append("]}");
        }
        return out.append("]").toString();
    }

    private static String html(List<Event> events) {
        String json = buildJson(events);

        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Dining Philosophers — Monitor</title>
<style>
:root {
  --bg:#f4f6fb; --card:#fff; --ink:#172033; --muted:#68738a;
  --line:#e3e8f1; --blue:#4776e6; --green:#16a36a;
  --orange:#e89b31; --red:#df5965;
}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--ink);
  font:14px/1.5 "Segoe UI",Arial,sans-serif}
header{padding:24px 30px;background:#172554;color:white}
header h1{margin:0;font-size:25px}
header p{margin:5px 0 0;color:#dbeafe}
main{max-width:1450px;margin:22px auto;padding:0 20px}
.card{background:var(--card);border:1px solid var(--line);
  border-radius:14px;padding:18px;margin-bottom:18px;
  box-shadow:0 3px 14px #1e293b08}
h2{font-size:17px;margin:0 0 14px}
.controls{display:flex;gap:9px;flex-wrap:wrap;align-items:center}
button,select{padding:9px 15px;border:1px solid var(--line);
  border-radius:8px;background:white;cursor:pointer;color:var(--ink)}
button.primary{background:var(--blue);color:white;border-color:var(--blue)}
button:hover{filter:brightness(.96)}
input[type=range]{width:100%;accent-color:var(--blue)}
.stats{display:grid;grid-template-columns:repeat(4,1fr);gap:12px}
.stat{background:#f8faff;border:1px solid var(--line);
  border-radius:10px;padding:13px}
.stat span{display:block;color:var(--muted);font-size:12px}
.stat strong{font-size:21px}
.layout{display:grid;grid-template-columns:minmax(300px,1.15fr)
  minmax(300px,1fr);gap:18px}
.table-area{position:relative;max-width:520px;margin:auto;width:100%;
  aspect-ratio:1}
.ring{position:absolute;width:53%;height:53%;left:23.5%;top:23.5%;
  border-radius:50%;border:12px solid #dce6f6;
  background:radial-gradient(circle,#f4f8ff,#e9f0fc)}
.center{position:absolute;left:32%;top:38%;width:36%;text-align:center}
.center strong{font-size:15px;display:block}
.center small{color:var(--muted)}
.phil{position:absolute;transform:translate(-50%,-50%);
  width:105px;text-align:center;padding:9px 5px;background:white;
  border:2px solid var(--line);border-radius:12px;z-index:2;
  box-shadow:0 3px 8px #17255410}
.phil .emoji{font-size:23px;display:block}
.phil b{display:block}
.phil small{font-size:11px;color:var(--muted)}
.phil.eating{border-color:var(--green);background:#e9fff4}
.phil.hungry{border-color:var(--orange);background:#fff7e7}
.fork{position:absolute;transform:translate(-50%,-50%);
  background:#eef2f8;border:1px solid #c9d3e2;border-radius:10px;
  padding:7px 9px;z-index:3;font-size:12px}
.fork.held{background:#dcfce7;border-color:#16a36a;color:#166534}
table{border-collapse:collapse;width:100%}
th,td{text-align:left;padding:10px 8px;border-bottom:1px solid var(--line)}
th{font-size:12px;color:var(--muted);font-weight:600}
td{font-size:13px}
.pill{padding:4px 8px;border-radius:20px;font-size:11px;font-weight:700}
.pill.THINKING{background:#eaf0ff;color:#3559ad}
.pill.HUNGRY{background:#fff1d6;color:#a46600}
.pill.EATING{background:#d9fbe9;color:#087443}
.timeline-row{display:grid;grid-template-columns:35px 1fr;gap:10px;
  align-items:center;margin:10px 0}
.lane{height:25px;background:#f0f3f9;border-radius:5px;position:relative;
  overflow:hidden}
.segment{position:absolute;height:100%;border-radius:4px;min-width:3px}
.think-seg{background:#a9bff9}.hungry-seg{background:#f5c66f}
.eat-seg{background:#5fd49c}
.legend{display:flex;gap:14px;flex-wrap:wrap;color:var(--muted);
  font-size:12px;margin-top:12px}
.dot{display:inline-block;width:10px;height:10px;border-radius:3px;
  margin-right:5px}
.log{max-height:310px;overflow:auto}
.log button{width:100%;display:flex;gap:12px;text-align:left;
  border:0;border-bottom:1px solid var(--line);border-radius:0;
  padding:9px 4px}
.log button.active{background:#eef4ff}
.log .time{color:var(--muted);min-width:48px;font-variant-numeric:tabular-nums}
.log .message{flex:1}
.notice{padding:11px 13px;border-radius:9px;background:#eafaf1;
  color:#11663e;font-weight:600}
.notice.warning{background:#fff1d6;color:#925d00}
.muted{color:var(--muted)}
@media(max-width:850px){.layout{grid-template-columns:1fr}
  .stats{grid-template-columns:repeat(2,1fr)}header{padding:20px}}
</style>
</head>
<body>
<header>
  <h1>Dining Philosophers — Monitor Solution</h1>
  <p>Operating Systems · Synchronization · Java synchronized / wait / notifyAll</p>
</header>
<main>
  <section class="card">
    <h2>Simulation Controls</h2>
    <div class="controls">
      <button id="play" class="primary">▶ Play</button>
      <button id="step">Step →</button>
      <button id="reset">↺ Reset</button>
      <label for="speed">Speed</label>
      <select id="speed">
        <option value="1200">0.5×</option>
        <option value="700" selected>1×</option>
        <option value="300">2×</option>
        <option value="100">4×</option>
      </select>
      <span class="muted" id="position">Event 1</span>
    </div>
    <input id="slider" type="range" min="0" value="0" step="1">
  </section>

  <section class="stats">
    <div class="stat"><span>Current event</span><strong id="eventCount">1</strong></div>
    <div class="stat"><span>Philosophers eating</span><strong id="eatingCount">0</strong></div>
    <div class="stat"><span>Meals completed</span><strong id="mealCount">0</strong></div>
    <div class="stat"><span>Simulation time</span><strong id="simTime">0 ms</strong></div>
  </section>

  <div class="layout" style="margin-top:18px">
    <section class="card">
      <h2>Circular Dining Table</h2>
      <div class="table-area" id="tableArea">
        <div class="ring"></div>
        <div class="center"><strong>SHARED TABLE</strong><small>Monitor lock</small></div>
      </div>
      <div id="deadlock" class="notice">Checking synchronization…</div>
    </section>

    <section class="card">
      <h2>Philosopher Status</h2>
      <table>
        <thead><tr><th>Philosopher</th><th>State</th><th>Meals</th><th>Forks</th></tr></thead>
        <tbody id="statusBody"></tbody>
      </table>
      <h2 style="margin-top:22px">Fork Ownership</h2>
      <table>
        <thead><tr><th>Fork</th><th>Between</th><th>Owner</th></tr></thead>
        <tbody id="forkBody"></tbody>
      </table>
    </section>
  </div>

  <section class="card">
    <h2>Execution Timeline</h2>
    <p class="muted">Each lane shows the state of one philosopher throughout the simulation.</p>
    <div id="timeline"></div>
    <div class="legend">
      <span><i class="dot" style="background:#a9bff9"></i>Thinking</span>
      <span><i class="dot" style="background:#f5c66f"></i>Hungry / waiting</span>
      <span><i class="dot" style="background:#5fd49c"></i>Eating</span>
    </div>
  </section>

  <section class="card">
    <h2>Event Log</h2>
    <p class="muted">Select an event to jump to that point in the simulation.</p>
    <div id="log" class="log"></div>
  </section>
</main>

<script>
const events = JSON_DATA;
let index = 0;
let timer = null;

const $ = id => document.getElementById(id);
const names = ["P0","P1","P2","P3"];
const colors = ["#4776e6","#16a36a","#e89b31","#9b72cf"];

function stop() {
  if (timer !== null) clearInterval(timer);
  timer = null;
  $("play").textContent = "▶ Play";
}

function makeTable() {
  const area = $("tableArea");
  for (let p=0;p<4;p++) {
    const angle = (-90 + p*90) * Math.PI / 180;
    const x = 50 + 40*Math.cos(angle);
    const y = 50 + 40*Math.sin(angle);
    const el = document.createElement("div");
    el.className = "phil";
    el.id = "phil"+p;
    el.style.left = x+"%";
    el.style.top = y+"%";
    el.innerHTML = '<span class="emoji">🧑‍🎓</span><b>P'+p+'</b><small>Thinking</small>';
    area.appendChild(el);
  }
  for (let f=0;f<4;f++) {
    const angle = (-45 + f*90) * Math.PI / 180;
    const x = 50 + 24*Math.cos(angle);
    const y = 50 + 24*Math.sin(angle);
    const el = document.createElement("div");
    el.className = "fork";
    el.id = "fork"+f;
    el.style.left = x+"%";
    el.style.top = y+"%";
    el.textContent = "🍴 F"+f;
    area.appendChild(el);
  }
}

function renderTimeline() {
  const last = events.length-1;
  const rows = names.map((name,p) => {
    let start=0, state=events[0].states[p], segments="";
    for (let i=1;i<=last;i++) {
      const next=events[i].states[p];
      if (next!==state || i===last) {
        const end=i;
        const width=(end-start)/Math.max(1,last)*100;
        const cls=state==="EATING"?"eat-seg":
                  state==="HUNGRY"?"hungry-seg":"think-seg";
        segments += '<span class="segment '+cls+'" title="'+state+
          '" style="left:'+(start/Math.max(1,last)*100)+
          '%;width:'+Math.max(width,0.4)+'%"></span>';
        start=i; state=next;
      }
    }
    return '<div class="timeline-row"><b style="color:'+colors[p]+'">'+
      name+'</b><div class="lane">'+segments+'</div></div>';
  });
  $("timeline").innerHTML=rows.join("");
}

function renderLog() {
  $("log").innerHTML=events.map((e,i)=>
    '<button data-index="'+i+'" class="'+(i===index?'active':'')+'">'+
    '<span class="time">'+e.time+' ms</span>'+
    '<span class="message">'+e.message+'</span></button>'
  ).join("");
  $("log").querySelectorAll("button").forEach(button=>{
    button.addEventListener("click",()=>{
      stop(); show(Number(button.dataset.index));
    });
  });
}

function show(i) {
  index=Math.max(0,Math.min(events.length-1,i));
  const e=events[index];
  $("slider").value=index;
  $("slider").max=events.length-1;
  $("position").textContent="Event "+(index+1)+" / "+events.length;
  $("eventCount").textContent=index+1;
  $("simTime").textContent=e.time+" ms";
  $("eatingCount").textContent=e.states.filter(s=>s==="EATING").length;
  $("mealCount").textContent=e.meals.reduce((a,b)=>a+b,0);

  for(let p=0;p<4;p++) {
    const el=$("phil"+p);
    el.className="phil "+e.states[p].toLowerCase();
    el.querySelector("small").textContent=e.states[p];
    const forks=[];
    for(let f=0;f<4;f++) if(e.forks[f]===p) forks.push(f);
    $("statusBody").rows[p].cells[1].innerHTML=
      '<span class="pill '+e.states[p]+'">'+e.states[p]+'</span>';
    $("statusBody").rows[p].cells[2].textContent=e.meals[p]+"/3";
    $("statusBody").rows[p].cells[3].textContent=
      forks.length?forks.map(f=>"F"+f).join(", "):"—";
  }

  for(let f=0;f<4;f++) {
    const owner=e.forks[f];
    $("fork"+f).classList.toggle("held",owner!==-1);
    $("fork"+f).textContent="🍴 F"+f+(owner!==-1?" → P"+owner:"");
    $("forkBody").rows[f].cells[2].textContent=
      owner===-1?"Available":"P"+owner;
  }

  const eating=e.states.filter(s=>s==="EATING").length;
  const invalid=e.states.some((s,p)=>s==="EATING" &&
    (e.states[(p+1)%4]==="EATING" ||
     e.states[(p+3)%4]==="EATING"));
  $("deadlock").className="notice"+(invalid?" warning":"");
  $("deadlock").textContent=invalid?
    "Synchronization warning: adjacent philosophers are eating.":
    eating===0?"No deadlock detected · No philosopher is currently eating.":
    "No deadlock detected · "+eating+
    " philosopher(s) eating · Adjacent philosophers never eat together.";

  $("log").querySelectorAll("button").forEach((b,j)=>
    b.classList.toggle("active",j===index));
}

function initTables() {
  $("statusBody").innerHTML=names.map((n,p)=>
    '<tr><td><b style="color:'+colors[p]+'">'+n+'</b></td>'+
    '<td></td><td></td><td></td></tr>').join("");
  $("forkBody").innerHTML=names.map((n,f)=>
    '<tr><td>F'+f+'</td><td>P'+f+' / P'+((f+1)%4)+
    '</td><td></td></tr>').join("");
}

$("play").addEventListener("click",()=>{
  if(timer!==null){stop();return;}
  if(index>=events.length-1)show(0);
  $("play").textContent="⏸ Pause";
  timer=setInterval(()=>{
    if(index>=events.length-1){stop();return;}
    show(index+1);
  },Number($("speed").value));
});
$("step").addEventListener("click",()=>{stop();show(index+1);});
$("reset").addEventListener("click",()=>{stop();show(0);});
$("slider").addEventListener("input",e=>{stop();show(Number(e.target.value));});

makeTable();
initTables();
renderTimeline();
renderLog();
show(0);
</script>
</body>
</html>
""".replace("JSON_DATA", json);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Dining Philosophers - Monitor Solution");
        System.out.println("Philosophers: " + N + ", Meals each: " + MEALS);

        Philosopher[] philosophers = new Philosopher[N];

        for (int i = 0; i < N; i++) {
            philosophers[i] = new Philosopher(i);
            philosophers[i].start();
        }

        for (Philosopher philosopher : philosophers) {
            philosopher.join();
        }

        List<Event> events = monitor.getEvents();
        int[] meals = monitor.getMeals();

        System.out.println("\nSimulation completed.");
        for (int i = 0; i < N; i++) {
            System.out.println("P" + i + " completed " + meals[i] + " meals.");
        }

        try {
            Path output = Path.of("generated_html",
                                  "dining_philosophers_monitor.html");
            Files.createDirectories(output.getParent());
            Files.writeString(output, html(events), StandardCharsets.UTF_8);

            System.out.println("\nHTML visualization generated:");
            System.out.println(output.toAbsolutePath());
            System.out.println("Open the HTML file in your browser.");
        } catch (IOException e) {
            System.err.println("Could not generate HTML: " + e.getMessage());
        }
    }
}
