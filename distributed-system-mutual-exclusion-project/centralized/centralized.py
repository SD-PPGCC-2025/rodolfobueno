import threading
import time

class Coordinator:
    def __init__(self):
        self.lock = threading.Lock()
        self.resource_in_use_by = None
        self.waiting_queue = []

    def request_access(self, process_id):
        with self.lock:
            if self.resource_in_use_by == process_id:
                print(f"[Coordinator] Process {process_id} already has access")
                return "granted"

            if self.resource_in_use_by is None:
                self.resource_in_use_by = process_id
                print(f"[Coordinator] Grant access → Process {process_id}")
                return "granted"
            else:
                print(f"[Coordinator] Deny access → Process {process_id} (resource held by {self.resource_in_use_by})")
                if process_id not in self.waiting_queue:
                    print(f"[Coordinator] Adding Process {process_id} to waiting queue")
                    self.waiting_queue.append(process_id)
                return "denied"

    def release_resource(self, process_id):
        with self.lock:
            if self.resource_in_use_by == process_id:
                print(f"[Coordinator] Process {process_id} released the resource")
                self.resource_in_use_by = None
                if self.waiting_queue:
                    next_process = self.waiting_queue.pop(0)
                    self.resource_in_use_by = next_process
                    print(f"[Coordinator] Now granting access to Process {next_process}")
                    return next_process
            else:
                print(f"[Coordinator] ERROR: Process {process_id} tried to release a resource it doesn’t hold")
        return None


class Process(threading.Thread):
    def __init__(self, process_id, coordinator):
        super().__init__()
        self.process_id = process_id
        self.coordinator = coordinator
        self.has_resource = False

    def run(self):
        print(f"[Process {self.process_id}] Starting and requesting access")
        while not self.has_resource:
            reply = self.coordinator.request_access(self.process_id)
            if reply == "granted":
                self.has_resource = True
                break
            else:
                print(f"[Process {self.process_id}] Access denied, waiting 2s before retrying...")
                time.sleep(2)

        print(f"[Process {self.process_id}] Accessing resource for 3s...")
        time.sleep(3)
        print(f"[Process {self.process_id}] Done. Releasing resource.")
        next_proc = self.coordinator.release_resource(self.process_id)
        if next_proc:
            print(f"[Process {next_proc}] Ready to retry after grant")


if __name__ == "__main__":
    coordinator = Coordinator()
    processes = [Process(i, coordinator) for i in range(1, 4)]  # 10 processes

    for p in processes:
        p.start()

    for p in processes:
        p.join()

    print("✅ Simulation complete.")
